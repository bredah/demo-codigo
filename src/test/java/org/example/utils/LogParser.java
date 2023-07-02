package org.example.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {
    private String logString;
    private List<LogEntry> logEntries;

    public LogParser(String logString) {
        this.logString = logString;
        parseLogString();
    }

    private void parseLogString() {
        logEntries = new ArrayList<>();

        String pattern = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) \\[(.*?)\\] (\\w+) (.*?) - (.*)";
        Matcher matcher = Pattern.compile(pattern).matcher(this.logString);

        while (matcher.find()) {
            String timestamp = matcher.group(1);
            String thread = matcher.group(2);
            String level = matcher.group(3);
            String logger = matcher.group(4);
            String message = matcher.group(5);

            LogEntry logEntry = new LogEntry(timestamp, thread, level, logger, message);
            logEntries.add(logEntry);
        }
    }

    public int size() {
        return logEntries.size();
    }

    public int findMessage(String message) {
        for (int i = 0; i < logEntries.size(); i++) {
            LogEntry logEntry = logEntries.get(i);
            if (logEntry.getMessage().equals(message)) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(String message) {
        return findMessage(message) > -1;
    }

    @Data
    @AllArgsConstructor
    static class LogEntry {
        private String timestamp;
        private String thread;
        private String level;
        private String logger;
        private String message;
    }
}