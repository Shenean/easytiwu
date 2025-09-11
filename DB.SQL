-- ==================== 删除旧表（顺序：子表 -> 父表） ====================
DROP TABLE IF EXISTS question_options;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS question_banks;

-- =================== question_banks ==========================
CREATE TABLE question_banks (
                                id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                name VARCHAR(255) NOT NULL,
                                description TEXT,
                                total_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '题目总数',
                                completed_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '已完成题数',
                                wrong_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '错题数',
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (id),
                                KEY idx_name (name),
                                KEY idx_created_at (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  ROW_FORMAT=DYNAMIC
  COMMENT='题库表';

-- ======================= questions ==========================
CREATE TABLE questions (
                           id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                           bank_id BIGINT UNSIGNED NOT NULL,
                           content TEXT NOT NULL COMMENT '题干',
                           type ENUM('single','multiple','fill_blank','true_false','short_answer')
        NOT NULL DEFAULT 'single' COMMENT '题型',
                           user_answer VARCHAR(1024) NULL DEFAULT NULL COMMENT '用户最近一次作答内容',
                           correct_answer VARCHAR(1024) NOT NULL DEFAULT '' COMMENT '正确答案',
                           analysis TEXT NULL COMMENT '题目解析',
                           is_completed TINYINT(1) NOT NULL DEFAULT 0 COMMENT '练习完成：0未 1已',
                           is_correct TINYINT(1) DEFAULT NULL COMMENT '最近一次答题是否正确：1对 0错 NULL未答',
                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (id),
                           KEY idx_bank_id (bank_id),
                           KEY idx_question_type (type),
                           KEY idx_bank_completed_correct (bank_id, is_completed, is_correct),
                           KEY idx_bank_question_type (bank_id, type),
                           CONSTRAINT fk_questions_bank
                               FOREIGN KEY (bank_id) REFERENCES question_banks (id)
                                   ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  ROW_FORMAT=DYNAMIC
  COMMENT='题目表';

-- ==================== question_options ======================
CREATE TABLE question_options (
                                  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                  question_id BIGINT UNSIGNED NOT NULL COMMENT '关联题目ID',
                                  option_content TEXT NOT NULL COMMENT '选项内容',
                                  sort_order CHAR(1) NOT NULL COMMENT '选项标识，单字符（如 A、B、C、D）',
                                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (id),
                                  KEY idx_question_id (question_id),
                                  KEY idx_question_sort (question_id, sort_order),
                                  CONSTRAINT fk_question_options_question
                                      FOREIGN KEY (question_id) REFERENCES questions (id)
                                          ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  ROW_FORMAT=DYNAMIC
  COMMENT='题目选项表';

-- ================== TRIGGERS ======================
DELIMITER $$

-- INSERT：新增题目计数
DROP TRIGGER IF EXISTS trg_questions_ai $$
CREATE TRIGGER trg_questions_ai
    AFTER INSERT ON questions
    FOR EACH ROW
BEGIN
    UPDATE question_banks
    SET total_count     = total_count + 1,
        completed_count = completed_count + IF(NEW.is_completed = 1, 1, 0),
        wrong_count     = wrong_count + IF(NEW.is_completed = 1 AND NEW.is_correct = 0, 1, 0)
    WHERE id = NEW.bank_id;
END $$

-- UPDATE：题目状态或归属变更计数
DROP TRIGGER IF EXISTS trg_questions_au $$
CREATE TRIGGER trg_questions_au
    AFTER UPDATE ON questions
    FOR EACH ROW
BEGIN
    DECLARE v_old_wrong INT DEFAULT 0;
    DECLARE v_new_wrong INT DEFAULT 0;
    DECLARE v_delta_completed INT DEFAULT 0;
    DECLARE v_delta_wrong INT DEFAULT 0;

    SET v_old_wrong       = IF(OLD.is_completed = 1 AND OLD.is_correct = 0, 1, 0);
    SET v_new_wrong       = IF(NEW.is_completed = 1 AND NEW.is_correct = 0, 1, 0);
    SET v_delta_completed = IF(NEW.is_completed = 1, 1, 0) - IF(OLD.is_completed = 1, 1, 0);
    SET v_delta_wrong     = v_new_wrong - v_old_wrong;

    IF NEW.bank_id = OLD.bank_id THEN
    UPDATE question_banks
    SET completed_count = completed_count + v_delta_completed,
        wrong_count     = wrong_count + v_delta_wrong
    WHERE id = NEW.bank_id;
    ELSE
    UPDATE question_banks
    SET total_count     = total_count - 1,
        completed_count = completed_count - IF(OLD.is_completed = 1, 1, 0),
        wrong_count     = wrong_count - v_old_wrong
    WHERE id = OLD.bank_id;

    UPDATE question_banks
    SET total_count     = total_count + 1,
        completed_count = completed_count + IF(NEW.is_completed = 1, 1, 0),
        wrong_count     = wrong_count + v_new_wrong
    WHERE id = NEW.bank_id;
END IF;
END $$

-- DELETE：删除题目计数
DROP TRIGGER IF EXISTS trg_questions_ad $$
CREATE TRIGGER trg_questions_ad
    AFTER DELETE ON questions
    FOR EACH ROW
BEGIN
    UPDATE question_banks
    SET total_count     = total_count - 1,
        completed_count = completed_count - IF(OLD.is_completed = 1, 1, 0),
        wrong_count     = wrong_count - IF(OLD.is_completed = 1 AND OLD.is_correct = 0, 1, 0)
    WHERE id = OLD.bank_id;
END $$

DELIMITER ;
