package me.shib.lib.trakr;

import org.apache.commons.text.StringEscapeUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class TrakrContent {

    private static final transient Parser parser = Parser.builder().build();
    private static final transient HtmlRenderer renderer = HtmlRenderer.builder().build();

    private final String markdownText;

    public TrakrContent(String markdownText) {
        this.markdownText = markdownText;
    }

    private static String convertToHTML(String commonmarkText) {
        Node document = parser.parse(commonmarkText);
        return renderer.render(document);
    }

    private static String simplifyHTML(String htmlContent) {
        htmlContent = htmlContent.replaceAll(">\\s+", ">");
        htmlContent = htmlContent.replaceAll("\\s+<", "<");
        return StringEscapeUtils.unescapeHtml4(htmlContent);
    }

    public static String simplifyContent(String content, Type type) {
        switch (type) {
            case Markdown:
                return content;
            case HTML:
                return simplifyHTML(content);
            case Jira:
                return content;
            default:
                return content;
        }
    }

    public String getMarkdownContent() {
        return markdownText;
    }

    public String getHtmlContent() {
        return convertToHTML(markdownText);
    }

    public String getJiraContent() {
        String jiraText = markdownText.replace("**", "*").replace("__", "*");
        jiraText = jiraText.replace("```", "{code}")
                .replace("~~~", "{code}");
        String[] urlSplit = jiraText.split("]\\(");
        StringBuilder urlCleanedText = new StringBuilder().append(urlSplit[0]);
        for (int i = 1; i < urlSplit.length; i++) {
            urlCleanedText.append("|").append(urlSplit[i].replaceFirst("\\)", "]"));
        }
        return urlCleanedText.toString();
    }

    public String getContent(Type type) {
        switch (type) {
            case Markdown:
                return markdownText;
            case HTML:
                return getHtmlContent();
            case Jira:
                return getJiraContent();
            default:
                return markdownText;
        }
    }

    public enum Type {
        Markdown, HTML, Jira
    }

}