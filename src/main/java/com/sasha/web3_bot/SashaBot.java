package com.sasha.web3_bot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SashaBot extends TelegramLongPollingBot {


    private String token = "6617010435:AAGy3vF3S3PZhR8EgyoSyDZ5xcLqy0aSVb0";
    private String botUsername = "web3_open_black_bot";

    public SashaBot(DefaultBotOptions botOptions){
        super(botOptions);
    }

    public SashaBot() {
        this(new DefaultBotOptions());
    }


    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            Long chatId = message.getChatId();

            String text = message.getText();

            String[] split = text.split("=");
            if (split.length <= 1){
                sendMsg("不处理该类指令",chatId);
                return;
            }

            String s = executeLinuxCmd(split[1]);
            sendMsg(s,chatId);
        }
    }


    //回复普通文本消息
    public void sendMsg(String text,Long chatId){
        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setText(text);
        try {
            execute(response);
        } catch (TelegramApiException e) {
        }
    }


    public String executeLinuxCmd(String cmd) {
        System.out.println("执行命令[ " + cmd + " ]");
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(cmd);
            String line;
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer out = new StringBuffer();
            while ((line = stdoutReader.readLine()) != null ) {
                out.append(line+"\n");
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            process.destroy();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        //梯子在自己电脑上就写127.0.0.1  软路由就写路由器的地址
        String proxyHost = "127.0.0.1";
        //端口根据实际情况填写，说明在上面，自己看
        int proxyPort = 7890;

        DefaultBotOptions botOptions = new DefaultBotOptions();
        botOptions.setProxyHost(proxyHost);
        botOptions.setProxyPort(proxyPort);
        //注意一下这里，ProxyType是个枚举，看源码你就知道有NO_PROXY,HTTP,SOCKS4,SOCKS5;
        botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);

        DefaultBotSession defaultBotSession = new DefaultBotSession();
        defaultBotSession.setOptions(botOptions);
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(defaultBotSession.getClass());


            //需要代理
            SashaBot bot = new SashaBot(botOptions);
            telegramBotsApi.registerBot(bot);
            //不需代理
//            SashaBot bot2 = new SashaBot();
//            telegramBotsApi.registerBot(bot2);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
