package com.tlj.mianshihou.model.dto.questionbankquestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量添加或移除题库题目关联请求
 *

 */
@Data
public class QuestionBankQuestionBatchRequest implements Serializable {

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id
     */
    private List<Long> questionIds;


    private static final long serialVersionUID = 1L;
}