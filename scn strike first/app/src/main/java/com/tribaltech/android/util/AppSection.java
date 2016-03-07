package com.tribaltech.android.util;

enum AppSection {
    LIVE_SCORE("LiveScore"), USER_STATS("UserStats"), GO_BOWLING(
            "GoBowling"), TELL_FRIEND("TellYourFriend");

    private String text;

    AppSection(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static AppSection fromString(String text) {
        if (text != null) {
            for (AppSection section : AppSection.values()) {
                if (text.equalsIgnoreCase(section.text)) {
                    return section;
                }
            }
        }
        return null;
    }
}