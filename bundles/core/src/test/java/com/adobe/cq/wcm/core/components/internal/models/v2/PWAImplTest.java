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

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.resourceresolver.MockHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.PWA;
import com.day.cq.commons.jcr.JcrConstants;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class PWAImplTest {

    private static final String SITES_PROJECT_PATH = "/content/mysite";
    private static final String SITES_PAGE_PATH = "/content/mysite/us/en";
    private ResourceResolver resolver;
    private MockHelper mockHelper;
    private PWA pwa;
    private Resource resource;
    private ModifiableValueMap mvp;

    @ClassRule
    public static final AemContext context = CoreComponentTestContext.createContext("/pwa", SITES_PAGE_PATH);

    @Before
    public void setUp() {
        resolver = context.resourceResolver();
        resource = spy(resolver.getResource(SITES_PAGE_PATH));
        mvp = resource.adaptTo(ModifiableValueMap.class);
    }

    @Test
    public void testPWAReturnsManifestPath() {
        pwa = resource.adaptTo(PWA.class);
        Assert.assertEquals(SITES_PROJECT_PATH + "/manifest.webmanifest", pwa.getManifestPath());
    }

    @Test
    public void testPWAReturnsProjectName() {
        pwa = resource.adaptTo(PWA.class);
        Assert.assertEquals("mysite", pwa.getProjectName());
    }

    @Test
    public void testProjectNameReturnsBlankIfResourceIsNotUnderSitesProject() throws NoSuchFieldException, IllegalAccessException {
        when(resource.getPath()).thenReturn("/");
        pwa = resource.adaptTo(PWA.class);
        Assert.assertEquals("", pwa.getProjectName());
    }

    @Test
    public void testReturnsProjectNameIfResourceIsSitesProject() throws NoSuchFieldException, IllegalAccessException {
        when(resource.getPath()).thenReturn("/foo/bar");
        pwa = resource.adaptTo(PWA.class);
        Assert.assertEquals("bar", pwa.getProjectName());
    }

    @Test
    public void testPWAReturnsServiceWorkerPath() {
        pwa = resource.adaptTo(PWA.class);
        Assert.assertEquals("/mysitesw.js", pwa.getServiceWorkerPath());
    }

    @Test
    public void testPWAReturnsFalseIfPWAOptionIsNotEnabled() {
        pwa = resource.adaptTo(PWA.class);
        Assert.assertFalse(pwa.isPWAEnabled());
    }

    @Test
    public void testPWAReturnsTrueIfPWAOptionIsNotEnabled() throws NoSuchFieldException, IllegalAccessException {
        when(resource.getPath()).thenReturn("/foo/bar/baz");
        ResourceResolver spyResolver = spy(resolver);
        when(resource.getResourceResolver()).thenReturn(spyResolver);
        Resource mockSitesProject = mock(Resource.class);
        when(spyResolver.getResource("/foo/bar/" + JcrConstants.JCR_CONTENT)).thenReturn(mockSitesProject);

        mvp.put("enablePWA", true);
        when(mockSitesProject.getValueMap()).thenReturn(mvp);

        pwa = resource.adaptTo(PWA.class);
        Assert.assertTrue(pwa.isPWAEnabled());
    }
}
