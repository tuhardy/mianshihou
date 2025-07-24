package com.tlj.mianshihou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tlj.mianshihou.model.dto.questionbankquestion.questionBankQuestionBatchRequest;
import com.tlj.mianshihou.model.dto.questionbankquestion.QuestionBankQuestionQueryRequest;
import com.tlj.mianshihou.model.entity.QuestionBankQuestion;
import com.tlj.mianshihou.model.entity.User;
import com.tlj.mianshihou.model.vo.QuestionBankQuestionVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题库题目关联服务
 *

 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add 对创建的数据进行校验
     */
    void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest);
    
    /**
     * 获取题库题目关联封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request);

    /**
     * 分页获取题库题目关联封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request);

    /**
     * 批量添加题库题目关联
     * @param questionIdList
     * @param questionBankId
     * @param loginUser
     */
    void batchAddQuestion(List<Long> questionIdList, Long questionBankId, User loginUser);

    /**
     * 批量移除题库题目关联
     * @param questionIds
     * @param questionBankId
     */

    void batchRemoveQuestion(List<Long> questionIds, Long questionBankId);
}
