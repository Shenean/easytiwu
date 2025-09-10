package com.easytiwu.servicecontent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easytiwu.servicecontent.dto.QuestionDTO;
import com.easytiwu.servicecontent.dto.QuestionOptionDTO;
import com.easytiwu.servicecontent.entity.Question;
import com.easytiwu.servicecontent.entity.QuestionOption;
import com.easytiwu.servicecontent.mapper.QuestionMapper;
import com.easytiwu.servicecontent.mapper.QuestionOptionMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionQueryService {

    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper optionMapper;

    public QuestionQueryService(QuestionMapper questionMapper, QuestionOptionMapper optionMapper) {
        this.questionMapper = questionMapper;
        this.optionMapper = optionMapper;
    }

    public List<QuestionDTO> queryQuestions(Long bankId, String type) {
        LambdaQueryWrapper<Question> qw = new LambdaQueryWrapper<>();
        qw.eq(Question::getBankId, bankId);
        if ("wrong".equalsIgnoreCase(type)) {
            qw.eq(Question::getIsCompleted, 1).eq(Question::getIsCorrect, 0);
        }
        // else: all -> no extra filter
        List<Question> questions = questionMapper.selectList(qw);
        if (questions.isEmpty()) return Collections.emptyList();

        // Fetch options for all questions in one query
        List<Long> qids = questions.stream().map(Question::getId).toList();
        LambdaQueryWrapper<QuestionOption> ow = new LambdaQueryWrapper<>();
        ow.in(QuestionOption::getQuestionId, qids);
        List<QuestionOption> options = optionMapper.selectList(ow);
        Map<Long, List<QuestionOption>> optionMap = options.stream()
                .collect(Collectors.groupingBy(QuestionOption::getQuestionId));

        // Map to DTO
        List<QuestionDTO> dtos = new ArrayList<>(questions.size());
        for (Question q : questions) {
            QuestionDTO dto = new QuestionDTO();
            dto.setId(q.getId());
            dto.setContent(q.getContent());
            dto.setType(q.getType());
            dto.setUserAnswer(q.getUserAnswer());
            dto.setCorrectAnswer(q.getCorrectAnswer());
            dto.setAnalysis(q.getAnalysis());
            dto.setIsCompleted(q.getIsCompleted());
            dto.setIsCorrect(q.getIsCorrect());

            // options mapping: convert to label/text
            List<QuestionOption> list = optionMap.getOrDefault(q.getId(), Collections.emptyList());
            List<QuestionOptionDTO> optDtos = list.stream()
                    .sorted(Comparator.comparing(QuestionOption::getSortOrder))
                    .map(opt -> {
                        QuestionOptionDTO od = new QuestionOptionDTO();
                        od.setLabel(opt.getSortOrder());
                        od.setText(opt.getOptionContent());
                        return od;
                    })
                    .collect(Collectors.toList());
            dto.setOptions(optDtos);
            dtos.add(dto);
        }
        return dtos;
    }
}