/**
 * Copyright (c) 2009-2012, Netbout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are PROHIBITED without prior written permission from
 * the author. This product may NOT be used anywhere and on any computer
 * except the server platform of netBout Inc. located at www.netbout.com.
 * Federal copyright law prohibits unauthorized reproduction by any means
 * and imposes fines up to $25,000 for violation. If you received
 * this code occasionally and without intent to use it, please report this
 * incident to the author by email.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.netbout.rest;

import com.netbout.rest.auth.FacebookRs;
import com.netbout.rest.jaxb.LongHelper;
import com.netbout.rest.jaxb.LongIdentity;
import com.netbout.spi.Helper;
import com.netbout.spi.Identity;
import com.netbout.spi.client.RestSession;
import com.rexsl.core.Manifests;
import com.rexsl.misc.CookieBuilder;
import com.rexsl.page.BasePage;
import com.rexsl.page.JaxbBundle;
import com.rexsl.page.Link;
import java.util.Collection;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * NbPage.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 * @todo #254 Somehow we should specify PORT in the cookie. Without this param
 *  the site doesn't work in localhost:9099 in Chrome. Works fine in Safari,
 *  but not in Chrome. see http://stackoverflow.com/questions/1612177
 */
@XmlRootElement(name = "page")
@XmlAccessorType(XmlAccessType.NONE)
@SuppressWarnings("PMD.TooManyMethods")
public class NbPage extends BasePage<NbPage, NbResource> {

    /**
     * Is this page searcheable?
     */
    private transient boolean srchbl;

    /**
     * The response builder to return.
     */
    private final transient Response.ResponseBuilder builder = Response.ok();

    /**
     * Collection of log events.
     */
    private transient Collection<String> log;

    /**
     * The page is searchable.
     * @param srch Is this page searcheable?
     * @return This object
     */
    public final NbPage searcheable(final boolean srch) {
        this.srchbl = srch;
        return this;
    }

    /**
     * Render it.
     * @return This object
     */
    public final NbPage render() {
        this.builder.entity(this);
        this.log = this.home().log().events();
        this.home().log().clear();
        return this;
    }

    /**
     * Add authentication information.
     * @param identity The user
     * @return This object
     */
    public final Response.ResponseBuilder authenticated(
        final Identity identity) {
        if (identity instanceof Helper) {
            this.append(new LongHelper(identity, (Helper) identity));
        } else {
            this.append(new LongIdentity(identity));
        }
        this.append(new JaxbBundle("auth", new Cryptor().encrypt(identity)));
        this.link(new Link("logout", "/g/out"));
        this.link(new Link("profile", "/pf"));
        if (this.trusted(identity)) {
            this.link(new Link("start", "/s"));
        } else {
            this.link(new Link("re-login", "/g/re"));
        }
        this.extend();
        return this.builder
            .header(
                HttpHeaders.SET_COOKIE,
                this.nocookie(RestSession.MESSAGE_COOKIE)
        )
            .cookie(
                new CookieBuilder(this.home().base())
                    .name(RestSession.LOG_COOKIE)
                    .value(this.home().log().toString())
                    .temporary()
                    .build()
            )
            .cookie(
                new CookieBuilder(this.home().base())
                    .name(RestSession.AUTH_COOKIE)
                    .value(new Cryptor().encrypt(identity))
                    .temporary()
                    .build()
            )
            .type(MediaType.TEXT_XML);
    }

    /**
     * It's anonymous.
     * @return This object
     */
    public final Response.ResponseBuilder anonymous() {
        this.link(new Link("login", "/g"));
        this.extend();
        return this.builder
            .header(
                HttpHeaders.SET_COOKIE,
                this.nocookie(RestSession.MESSAGE_COOKIE)
        )
            .header(
                HttpHeaders.SET_COOKIE,
                this.nocookie(RestSession.LOG_COOKIE)
            )
            .header(
                HttpHeaders.SET_COOKIE,
                this.nocookie(RestSession.AUTH_COOKIE)
            )
            .type(MediaType.TEXT_XML);
    }

    /**
     * Preserve authentication status.
     * @return This object
     */
    public final Response.ResponseBuilder preserved() {
        Response.ResponseBuilder bldr;
        try {
            bldr = this.authenticated(this.home().identity());
        } catch (LoginRequiredException ex) {
            bldr = this.anonymous();
        }
        return bldr;
    }

    /**
     * Get all log events.
     * @return Full list of events
     */
    @XmlElement(name = "event")
    @XmlElementWrapper(name = "log")
    public final Collection<String> getLog() {
        return this.log;
    }

    /**
     * Is this page searcheable?
     * @return Yes or no
     */
    @XmlAttribute
    public final boolean isSearcheable() {
        return this.srchbl;
    }

    /**
     * Can we fully trust this guy or he should re-login?
     * @param identity The person
     * @return Trusted?
     * @todo #249 We should find a better place for this method, and its
     *  implementation is just a skeleton for now. We should implement it
     *  somehow properly.
     */
    public static boolean trusted(final Identity identity) {
        final String[] nids = new String[] {
            FacebookRs.NAMESPACE,
            "test",
            "woquo",
            "caybo",
            "netbout",
        };
        boolean trusted = false;
        final String nid = identity.name().nid();
        for (String good : nids) {
            if (nid.equals(good)) {
                trusted = true;
                break;
            }
        }
        return trusted;
    }

    /**
     * Extend page with mandatory elements.
     */
    private void extend() {
        this.append(
            new JaxbBundle("version")
                .add("name", Manifests.read("Netbout-Version"))
                .up()
                // @checkstyle MultipleStringLiterals (1 line)
                .add("revision", Manifests.read("Netbout-Revision"))
                .up()
                .add("date", Manifests.read("Netbout-Date"))
                .up()
        );
        this.append(new JaxbBundle("message", this.home().message()));
        final String qauth = this.home().qauth();
        if (!qauth.isEmpty()) {
            for (Link link : this.getLinks()) {
                link.setHref(
                    UriBuilder.fromUri(link.getHref())
                        .queryParam(RestSession.AUTH_PARAM, "{qauth}")
                        .build(qauth)
                        .toString()
                );
            }
        }
    }

    /**
     * Create header that cleans cookie with the given name.
     * @param name Name of the cookie
     * @return Value of the HTTP header
     */
    private String nocookie(final String name) {
        return new CookieBuilder(this.home().base())
            .name(name)
            .path(
                String.format(
                    "/%s",
                    this.home().httpServletRequest().getContextPath()
                )
            )
            .build()
            .toString();
    }

}
