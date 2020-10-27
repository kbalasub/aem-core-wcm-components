/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.adobe.cq.wcm.core.components.internal.models.v2;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.adobe.cq.wcm.core.components.models.PWA;
import com.day.cq.commons.jcr.JcrConstants;

@Model(adaptables = Resource.class,
    adapters = {PWA.class})
public class PWAImpl implements PWA {

    static final String MANIFEST_NAME = "manifest.webmanifest";

    private boolean isPWAEnabled = false;
    private String projectName = "";
    private String manifestPath = "";
    private String serviceWorkerPath = "";

    @Self
    private Resource resource;

    @PostConstruct
    protected void initModel() {
        String projectPath = this.getSitesProjectPath(resource.getPath());
        Resource project = resource.getResourceResolver().getResource(projectPath + JcrConstants.JCR_CONTENT);

        if (project != null) {
            ValueMap valueMap = project.getValueMap();
            Boolean isPWAEnabled = valueMap.get("enablePWA", Boolean.class);
            this.isPWAEnabled = (isPWAEnabled != null) ? isPWAEnabled : false;
        }

        String[] levels = projectPath.split("/");
        this.projectName = levels[levels.length - 1];
        this.manifestPath = projectPath + MANIFEST_NAME;
        this.serviceWorkerPath = "/" + this.projectName + "sw.js";
    }

    @Override
    public boolean isPWAEnabled() {
        return this.isPWAEnabled;
    }

    @Override
    public String getProjectName() {
        return this.projectName;
    }

    @Override
    public String getManifestPath() {
        return this.manifestPath;
    }

    @Override
    public String getServiceWorkerPath() {
        return this.serviceWorkerPath;
    }

    @Nonnull
    private String getSitesProjectPath(String path) {
        String[] levels = path.split("/");

        if (levels.length < 3) {
            return "";
        }

        if (levels.length == 3) {
            return path;
        }

        int i = 0;
        StringBuilder projectPath = new StringBuilder();
        while (i < 3) {
            projectPath.append(levels[i]).append('/');
            i++;
        }

        return projectPath.toString();
    }
}
