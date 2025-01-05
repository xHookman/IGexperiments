package com.chacha.igexperiments;

public class Contributor {
    private String name;
    private String githubUrl;
    private String linkedinUrl;
    private String telegramUrl;

    public Contributor(String name, String githubUrl, String linkedinUrl, String telegramUrl) {
        this.name = name;
        this.githubUrl = githubUrl;
        this.linkedinUrl = linkedinUrl;
        this.telegramUrl = telegramUrl;
    }

    public String getName() {
        return name;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public String getTelegramUrl() {
        return telegramUrl;
    }
}
