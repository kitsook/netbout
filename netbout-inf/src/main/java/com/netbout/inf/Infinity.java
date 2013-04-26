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
package com.netbout.inf;

import com.jcabi.urn.URN;
import com.netbout.spi.Query;
import java.io.Closeable;
import java.util.Set;

/**
 * Infinity, with information about bouts and messages.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
public interface Infinity extends Closeable {

    /**
     * How long do we need to wait before sending requests?
     * @param who Who is asking (empty list means that we need information
     *  for the entire Infinity, not any particular URN)
     * @return Estimated number of nanoseconds
     */
    long eta(URN... who);

    /**
     * Find messages for the given predicate.
     * @param query The predicate to use
     * @return The list of messages, ordered
     * @throws InvalidSyntaxException If query syntax is not valid
     */
    Iterable<Long> messages(Query query) throws InvalidSyntaxException;

    /**
     * How many messages were seen totally by this infinity?
     * @return Maximum number of the message seen so far
     */
    long maximum();

    /**
     * Send this notice to infinity.
     * @param notice The notice to see
     * @return Who should wait for its processing
     */
    Set<URN> see(Notice notice);

}