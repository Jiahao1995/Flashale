package com.example.flashale.web;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class TestController
{
    @ResponseBody
    @RequestMapping("hello")
    public String hello()
    {
        String result;
        try (Entry entry = SphU.entry("HelloResource")) {
            result = "Hello Sentinel";
            return result;
        }
        catch (BlockException ex) {
            log.error(ex.toString());
            result = "System busy, try again later";
            return result;
        }
    }

//    @PostConstruct
//    public void initFlowRules()
//    {
//        // 1. list of flow rules
//        List<FlowRule> rules = new ArrayList<>();
//        // 2. create flow rule
//        FlowRule rule = new FlowRule();
//        rule.setResource("HelloResource");
//        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
//        rule.setCount(2);
//        // 3. put rule in list
//        rules.add(rule);
//        // 4. load rule
//        FlowRuleManager.loadRules(rules);
//    }

    @PostConstruct
    public void flashaleFlow()
    {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("flashale");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(1);

        FlowRule rule2 = new FlowRule();
        rule2.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule2.setCount(2);
        rule2.setResource("HelloResource");

        rules.add(rule);
        rules.add(rule2);

        FlowRuleManager.loadRules(rules);
    }
}
