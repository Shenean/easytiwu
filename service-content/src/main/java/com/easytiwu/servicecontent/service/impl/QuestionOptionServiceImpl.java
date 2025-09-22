package com.easytiwu.servicecontent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easytiwu.servicecontent.dto.QuestionOptionDTO;
import com.easytiwu.servicecontent.entity.QuestionOption;
import com.easytiwu.servicecontent.mapper.QuestionOptionMapper;
import com.easytiwu.servicecontent.service.QuestionOptionServiceInterface;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 题目选项服务实现
 *
 * @author lingma
 */
@Service
public class QuestionOptionServiceImpl implements QuestionOptionServiceInterface {

    private final QuestionOptionMapper optionMapper;

    public QuestionOptionServiceImpl(QuestionOptionMapper optionMapper) {
        this.optionMapper = optionMapper;
    }

    @Override
    public Map<Long, List<QuestionOption>> getOptionsMap(List<Long> questionIds) {
        if (questionIds.isEmpty()) {
            return Collections.emptyMap();
        }

        LambdaQueryWrapper<QuestionOption> ow = new LambdaQueryWrapper<>();
        ow.in(QuestionOption::getQuestionId, questionIds);
        List<QuestionOption> options = optionMapper.selectList(ow);
        
        return options.stream()
                .collect(Collectors.groupingBy(QuestionOption::getQuestionId));
    }

    @Override
    public List<QuestionOptionDTO> convertToDTO(List<QuestionOption> options) {
        if (options.isEmpty()) {
            return Collections.emptyList();
        }

        return options.stream()
                .sorted(Comparator.comparing(QuestionOption::getSortOrder))
                .map(opt -> {
                    QuestionOptionDTO od = new QuestionOptionDTO();
                    od.setLabel(opt.getSortOrder());
                    od.setText(opt.getOptionContent());
                    return od;
                })
                .toList();
    }
}