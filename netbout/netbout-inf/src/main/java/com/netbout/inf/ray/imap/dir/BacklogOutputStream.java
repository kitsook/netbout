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
package com.netbout.inf.ray.imap.dir;

import com.jcabi.log.Logger;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.validation.constraints.NotNull;

/**
 * Output stream with {@link Backlog} items.
 *
 * <p>The class is thread-safe.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
final class BacklogOutputStream extends DataOutputStream {

    /**
     * Public ctor.
     * @param file The file to use
     * @throws IOException If some I/O problem inside
     */
    public BacklogOutputStream(@NotNull final File file) throws IOException {
        super(new FileOutputStream(file));
        this.writeInt(Backlog.START_MARKER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        this.writeUTF(Backlog.EOF_MARKER);
        this.writeUTF(Backlog.EOF_MARKER);
        super.close();
        Logger.debug(
            this,
            "#close(): saved %d bytes",
            this.written
        );
    }

    /**
     * Add new item.
     * @param item The items to add
     * @return Position where this writing happened
     * @throws IOException If some I/O problem inside
     */
    public long write(@NotNull final Backlog.Item item) throws IOException {
        final long pos = this.written;
        this.writeUTF(item.value());
        this.writeUTF(item.path());
        return pos;
    }

}
