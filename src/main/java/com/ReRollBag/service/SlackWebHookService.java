package com.ReRollBag.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class SlackWebHookService {

    public void publishMessageToSlack(String messages) {
        SlackApi slackApi = new SlackApi("https://hooks.slack.com/services/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");
        log.info("publishMessageToSlack!");
        slackApi.call(new SlackMessage(messages));
    }

}
