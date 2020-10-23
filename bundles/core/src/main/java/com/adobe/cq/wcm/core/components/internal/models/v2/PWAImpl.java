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

    @Self
    private Resource resource;

    @Override
    public boolean isPWAEnabled() {
        String projectPath = this.getSitesProjectPath(resource.getPath());
        Resource project = resource.getResourceResolver().getResource(projectPath + JcrConstants.JCR_CONTENT);

        if (project == null) {
            return false;
        }

        ValueMap valueMap = project.getValueMap();
        return valueMap.get("enablePWA", Boolean.class);
    }

    @Override
    public String getProjectName() {
        String projectPath = this.getSitesProjectPath(resource.getPath());
        String[] levels = projectPath.split("/");
        return levels[levels.length - 1];
    }

    @Override
    public String getManifestPath() {
        return this.getSitesProjectPath(resource.getPath()) + MANIFEST_NAME;
    }

    @Override
    public String getServiceWorkerPath() {
        return "/" + this.getProjectName() + "sw.js";
    }

    @Nonnull
    private String getSitesProjectPath(String path) {
        String[] levels = path.split("/");

        if (levels.length < 3) {
            return "";
        };

        if (levels.length == 3) {
            return path;
        };

        int i = 0;
        StringBuilder projectPath = new StringBuilder();
        while (i < 3) {
            projectPath.append(levels[i]).append('/');
            i++;
        }

        return projectPath.toString();
    }
}
