package com.tlj.mianshihou.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tlj.mianshihou.common.ErrorCode;
import com.tlj.mianshihou.constant.CommonConstant;
import com.tlj.mianshihou.exception.BusinessException;
import com.tlj.mianshihou.exception.ThrowUtils;
import com.tlj.mianshihou.mapper.QuestionBankQuestionMapper;

import com.tlj.mianshihou.model.dto.questionbankquestion.questionBankQuestionBatchRequest;
import com.tlj.mianshihou.model.dto.questionbankquestion.QuestionBankQuestionQueryRequest;
import com.tlj.mianshihou.model.entity.Question;
import com.tlj.mianshihou.model.entity.QuestionBank;
import com.tlj.mianshihou.model.entity.QuestionBankQuestion;

import com.tlj.mianshihou.model.entity.User;
import com.tlj.mianshihou.model.vo.QuestionBankQuestionVO;
import com.tlj.mianshihou.model.vo.UserVO;
import com.tlj.mianshihou.service.QuestionBankQuestionService;
import com.tlj.mianshihou.service.QuestionBankService;
import com.tlj.mianshihou.service.QuestionService;
import com.tlj.mianshihou.service.UserService;
import com.tlj.mianshihou.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题库题目关联服务实现
 *

 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Resource
    private UserService userService;
    @Resource
    private QuestionBankService questionBankService;
    @Resource
    private QuestionService questionService;

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);
        // 题目和题库必须存在
        Long questionId = questionBankQuestion.getQuestionId();
        if (questionId != null) {
            Question question = questionService.getById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        Long questionBankId = questionBankQuestion.getQuestionBankId();
        if (questionBankId != null) {
            QuestionBank questionBank = questionBankService.getById(questionBankId);
            ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");
        }
    }

    /**
     * 获取查询条件
     * @param questionBankQuestionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQuestionQueryRequest.getId();
        Long notId = questionBankQuestionQueryRequest.getNotId();
        String sortField = questionBankQuestionQueryRequest.getSortField();
        String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();
        Long userId = questionBankQuestionQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId), "questionBankId", questionBankId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题库题目关联封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionBankQuestionVO = QuestionBankQuestionVO.objToVo(questionBankQuestion);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionBankQuestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankQuestionVO.setUser(userVO);
       /* // 2. 已登录，获取用户点赞、收藏状态
        long questionBankQuestionId = questionBankQuestion.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<QuestionBankQuestionThumb> questionBankQuestionThumbQueryWrapper = new QueryWrapper<>();
            questionBankQuestionThumbQueryWrapper.in("questionBankQuestionId", questionBankQuestionId);
            questionBankQuestionThumbQueryWrapper.eq("userId", loginUser.getId());
            QuestionBankQuestionThumb questionBankQuestionThumb = questionBankQuestionThumbMapper.selectOne(questionBankQuestionThumbQueryWrapper);
            questionBankQuestionVO.setHasThumb(questionBankQuestionThumb != null);
            // 获取收藏
            QueryWrapper<QuestionBankQuestionFavour> questionBankQuestionFavourQueryWrapper = new QueryWrapper<>();
            questionBankQuestionFavourQueryWrapper.in("questionBankQuestionId", questionBankQuestionId);
            questionBankQuestionFavourQueryWrapper.eq("userId", loginUser.getId());
            QuestionBankQuestionFavour questionBankQuestionFavour = questionBankQuestionFavourMapper.selectOne(questionBankQuestionFavourQueryWrapper);
            questionBankQuestionVO.setHasFavour(questionBankQuestionFavour != null);
        }*/
        // endregion

        return questionBankQuestionVO;
    }

    /**
     * 分页获取题库题目关联封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(questionBankQuestionPage.getCurrent(), questionBankQuestionPage.getSize(), questionBankQuestionPage.getTotal());
        if (CollUtil.isEmpty(questionBankQuestionList)) {
            return questionBankQuestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList.stream().map(questionBankQuestion -> {
            return QuestionBankQuestionVO.objToVo(questionBankQuestion);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionBankQuestionList.stream().map(QuestionBankQuestion::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        questionBankQuestionVOList.forEach(questionBankQuestionVO -> {
            Long userId = questionBankQuestionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankQuestionVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
        return questionBankQuestionVOPage;
    }

    /**
     * 批量添加题库题目关联
     * @param questionIdList
     * @param questionBankId
     * @param loginUser
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddQuestion(List<Long> questionIdList, Long questionBankId, User loginUser) {
        // 校验题目和题库是否存在
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表为空");
        ThrowUtils.throwIf(loginUser ==null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(questionBankId==null||questionBankId>=0, ErrorCode.PARAMS_ERROR, "题库非法");
        List<Question> questionList = questionService.listByIds(questionIdList);
        List<Long> validQuestionIds = questionList.stream().map(Question::getId).collect(Collectors.toList());
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIds), ErrorCode.NOT_FOUND_ERROR,"合法的题目不存在");
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank==null, ErrorCode.NOT_FOUND_ERROR, "该题库不存在");
        // 批量添加，要么全部成功，要么全部失败
        List<QuestionBankQuestion> questionBankQuestionList = questionList.stream().map(question -> {
            QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
            questionBankQuestion.setQuestionId(question.getId());
            questionBankQuestion.setQuestionBankId(questionBank.getId());
            questionBankQuestion.setUserId(loginUser.getId());
            return questionBankQuestion;
        }).collect(Collectors.toList());
        boolean result = this.saveBatch(questionBankQuestionList);

        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "批量添加失败");
        //可能会部分添加成功，因为每个save方法都是一个事务
        //for (Long questionId : validQuestionIdList) {
        //        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
        //        questionBankQuestion.setQuestionBankId(questionBankId);
        //        questionBankQuestion.setQuestionId(questionId);
        //        questionBankQuestion.setUserId(loginUser.getId());
        //        boolean result = this.save(questionBankQuestion);
        //        if (!result) {
        //            throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
        //        }
        //    }
    }

    /**
     * 批量移除题库题目关联
     * @param questionIdList
     * @param questionBankId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveQuestion(List<Long> questionIdList, Long questionBankId) {
        // 校验题目和题库是否存在
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表为空");
        ThrowUtils.throwIf(questionBankId==null||questionBankId>=0, ErrorCode.PARAMS_ERROR, "题库非法");
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        // 批量添加，要么全部成功，要么全部失败
        for (Long questionId : questionIdList) {
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
            boolean result = this.remove(lambdaQueryWrapper);
            if (!result) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "从题库移除题目失败")
            }
        }
    }
}
