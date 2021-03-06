/*
 * $HeadURL: https://svn.apache.org/repos/asf/httpcomponents/httpcore/tags/4.0.1/httpcore/src/test/java/org/apache/http/entity/TestAbstractHttpEntity.java $
 * $Revision: 744517 $
 * $Date: 2009-02-14 17:39:33 +0100 (Sat, 14 Feb 2009) $
 * 
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.entity;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.mockup.HttpEntityMockup;
import org.apache.http.protocol.HTTP;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for {@link AbstractHttpEntity}.
 *
 */
public class TestAbstractHttpEntity extends TestCase {

    public TestAbstractHttpEntity(String testName) {
        super(testName);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestAbstractHttpEntity.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public static Test suite() {
        return new TestSuite(TestAbstractHttpEntity.class);
    }

    public void testContentType() throws Exception {
        HttpEntityMockup httpentity = new HttpEntityMockup();
        httpentity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, HTTP.PLAIN_TEXT_TYPE));
        assertEquals(HTTP.CONTENT_TYPE, httpentity.getContentType().getName());
        assertEquals(HTTP.PLAIN_TEXT_TYPE, httpentity.getContentType().getValue());

        httpentity.setContentType(HTTP.PLAIN_TEXT_TYPE);
        assertEquals(HTTP.CONTENT_TYPE, httpentity.getContentType().getName());
        assertEquals(HTTP.PLAIN_TEXT_TYPE, httpentity.getContentType().getValue());

        httpentity.setContentType((Header)null);
        assertNull(httpentity.getContentType());
        httpentity.setContentType((String)null);
        assertNull(httpentity.getContentType());
    }

    public void testContentEncoding() throws Exception {
        HttpEntityMockup httpentity = new HttpEntityMockup();
        httpentity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, "gzip"));
        assertEquals(HTTP.CONTENT_ENCODING, httpentity.getContentEncoding().getName());
        assertEquals("gzip", httpentity.getContentEncoding().getValue());

        httpentity.setContentEncoding("gzip");
        assertEquals(HTTP.CONTENT_ENCODING, httpentity.getContentEncoding().getName());
        assertEquals("gzip", httpentity.getContentEncoding().getValue());

        httpentity.setContentEncoding((Header)null);
        assertNull(httpentity.getContentEncoding());
        httpentity.setContentEncoding((String)null);
        assertNull(httpentity.getContentEncoding());
    }

    public void testChunkingFlag() throws Exception {
        HttpEntityMockup httpentity = new HttpEntityMockup();
        assertFalse(httpentity.isChunked());
        httpentity.setChunked(true);
        assertTrue(httpentity.isChunked());
    }
    
    public void testConsumeContent() throws Exception {
        HttpEntityMockup httpentity = new HttpEntityMockup();
        httpentity.setStreaming(true);
        try {
            httpentity.consumeContent();
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        httpentity.setStreaming(false);
        httpentity.consumeContent();
    }
    
}
