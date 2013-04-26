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
 * this code accidentally and without intent to use it, please report this
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
package com.netbout.hub.inf;

import com.jcabi.urn.URN;
import com.netbout.hub.ParticipantDt;
import com.netbout.spi.Friend;
import com.netbout.spi.Participant;
import com.netbout.spi.Profile;

/**
 * Participant to be seen by INF.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
public final class InfParticipant implements Participant {

    /**
     * The data.
     */
    private final transient ParticipantDt data;

    /**
     * Public ctor.
     * @param dat The data
     */
    public InfParticipant(final ParticipantDt dat) {
        this.data = dat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Friend friend) {
        throw new UnsupportedOperationException("#compareTo()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        throw new UnsupportedOperationException("#toString()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URN name() {
        return this.data.getIdentity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void kickOff() {
        throw new UnsupportedOperationException("#kickOff()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean confirmed() {
        return this.data.isConfirmed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean leader() {
        return this.data.isLeader();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void consign() {
        throw new UnsupportedOperationException("#consign()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Profile profile() {
        throw new UnsupportedOperationException("#profile()");
    }

}