/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.blog.business.build.control;

import com.sourcecoding.blog.business.build.entity.BlogEntry;
import com.sourcecoding.blog.business.configuration.entity.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mre
 */
public class ContentBuilder {

    private static final int RSS_MAX_ARTICLES = 10;
    List<BlogEntry> entries;
    Map<String, Object> data = new HashMap<>();
    Configuration config;

    public ContentBuilder(List<BlogEntry> entries, Configuration config) {
        this.config = config;
        this.entries = entries;

        data.put("blogRoot", config.getBlogPath());
    }

    public void createBlogEntries(Template template) throws Exception {
        data.put("isIndexPage", false);

        for (BlogEntry entry : entries) {

            data.put("entry", entry);
            String articleDirPath = config.getHtmlExportRootDirectoryPath() + File.separator + entry.getKey();

            new File(articleDirPath).mkdir();

            String filePath = articleDirPath + File.separator + "index.html";
            System.out.println("create " + filePath);
            try (Writer file = new FileWriter(new File(filePath))) {
                template.process(data, file);
                file.flush();
            }
        }
    }

    public void createIndex(Template template, int maxNumberOfArticleInList) throws Exception {

        data.put("isIndexPage", true);

        data.put("entry", entries.get(0)); //first one

        int numberOfArticeList = (entries.size() < maxNumberOfArticleInList) ? entries.size() : maxNumberOfArticleInList;
        data.put("entries", entries.subList(1, numberOfArticeList));

        String filePath = config.getHtmlExportRootDirectoryPath() + File.separator + "index.html";
        System.out.println("create " + filePath);
        try (Writer file = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), "UTF-8"))) {
            template.process(data, file);

            file.flush();
        }
    }

    public void createSiteMapXML(Template template) throws Exception {
        data.put("entries", entries);
        writeFile(template, "sitemap.xml");
    }

    public void createRSS(Template template) throws Exception {
        int numberOfArticeList = (entries.size() < RSS_MAX_ARTICLES) ? entries.size() : RSS_MAX_ARTICLES;
        data.put("entries", entries.subList(0, numberOfArticeList));

        data.put("createdTime", new Date());
        writeFile(template, "rss2.xml");
    }

    private void writeFile(Template template, String filename) throws TemplateException, IOException {
        String filePath = config.getHtmlExportRootDirectoryPath() + File.separator + filename;
        System.out.println("create " + filePath);
        try (Writer file = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), "UTF-8"))) {
            template.process(data, file);

            file.flush();
        }
    }
}
