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
package com.netbout.inf;

import com.netbout.spi.Message;
import com.netbout.spi.NetboutUtils;
import com.ymock.util.Logger;
import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 * Store of all known predicates.
 *
 * <p>This class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
final class PredicateStore implements Closeable {

    /**
     * The folder to work with.
     */
    private final transient Folder folder = new EbsVolume();

    /**
     * Pointers to all known predicates.
     */
    private final transient Set<Pointer> pointers;

    /**
     * Public ctor.
     * @param idx The index
     */
    public PredicateStore(final Index idx) {
        this.index = idx;
        this.pointers = this.discover();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws java.io.IOException {
        for (Pointer pointer : this.pointers) {
            IOUtils.closeQuietly(pointer);
        }
    }

    /**
     * See this message.
     * @param msg The message
     */
    public void see(final Message msg) {
        for (Pointer pointer : this.pointers) {
            pointer.see(msg);
        }
    }

    /**
     * Build a predicate from name and list of preds.
     *
     * <p>Throws {@link PredicateException} if this name is not recognized.
     *
     * @param name Its name
     * @param atoms List of arguments
     * @return The predicate
     */
    public Predicate build(final String name, final List<Atom> atoms) {
        Predicate predicate = null;
        for (Pointer ptr : this.pointers) {
            if (ptr.pointsTo(name)) {
                predicate = ptr.build(name, atoms);
                break;
            }
        }
        if (predicate == null) {
            throw new PredicateException(
                String.format("Unknown predicate name '%s'", name)
            );
        }
        return predicate;
    }

    /**
     * Discover all predicates.
     * @return List of pointers to predicates
     */
    private Set<Pointer> discover() {
        final Set<Pointer> ptrs = new HashSet<Pointer>();
        ptrs.addAll(PredicatePointer.discover());
        ptrs.addAll(this.motors());
        return ptrs;
    }

    /**
     * Discover all motors.
     * @return List of pointers to predicates
     */
    private Set<Pointer> motors() {
        final Reflections ref = new Reflections(
            this.getClass().getPackage().getName()
        );
        final Set<Pointer> motors = new HashSet<Pointer>();
        for (Class pred : ref.getSubTypesOf(Pointer.class)) {
            final File dir = new File(this.folder.path(), pre.getName());
            dir.mkdirs();
            try {
                motors.add(
                    (Pointer) pred.getConstructor(File.class).newInstance(dir)
                );
            } catch (NoSuchMethodException ex) {
                throw new PredicateException(ex);
            } catch (InstantiationException ex) {
                throw new PredicateException(ex);
            } catch (IllegalAccessException ex) {
                throw new PredicateException(ex);
            } catch (java.lang.reflect.InvocationTargetException ex) {
                throw new PredicateException(ex);
            }
        }
        Logger.debug(
            this,
            "#discover(): %d motors discovered in classpath: %[list]s",
            motors.size(),
            motors
        );
        return motors;
    }

}
