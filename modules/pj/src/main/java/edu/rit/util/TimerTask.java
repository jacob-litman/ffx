//******************************************************************************
//
// File:    TimerTask.java
// Package: edu.rit.util
// Unit:    Interface edu.rit.util.TimerTask
//
// This Java source file is copyright (C) 2002-2004 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the Parallel Java Library ("PJ"). PJ is free
// software; you can redistribute it and/or modify it under the terms of the GNU
// General Public License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// PJ is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
// A PARTICULAR PURPOSE. See the GNU General Public License for more details.
//
// Linking this library statically or dynamically with other modules is making a
// combined work based on this library. Thus, the terms and conditions of the GNU
// General Public License cover the whole combination.
//
// As a special exception, the copyright holders of this library give you
// permission to link this library with independent modules to produce an
// executable, regardless of the license terms of these independent modules, and
// to copy and distribute the resulting executable under terms of your choice,
// provided that you also meet, for each linked independent module, the terms
// and conditions of the license of that module. An independent module is a module
// which is not derived from or based on this library. If you modify this library,
// you may extend this exception to your version of the library, but you are not
// obligated to do so. If you do not wish to do so, delete this exception
// statement from your version.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************
package edu.rit.util;

/**
 * Interface TimerTask specifies the interface for an object that performs timed
 * actions under the control of a {@linkplain Timer}.
 * <P>
 * When a timer is created, it is associated with a timer task. When the timer
 * becomes triggered -- that is, when the time comes to do the timed actions --
 * the timer calls the timer task's <code>action()</code> method. The timer passes a
 * reference to itself as an argument to the timer task's <code>action()</code>
 * method.
 * <P>
 * The first thing the timer task must do in the <code>action()</code> method is
 * check whether the timer is still triggered. If it is, the <code>action()</code>
 * method can perform its processing. But if the timer is no longer triggered,
 * the <code>action()</code> method must return without doing anything.
 * <P>
 * This is to deal with a race condition that can arise when multiple threads
 * are involved. Suppose the timer thread triggers the timer, the timer calls
 * the timer task's <code>action()</code> method, and the <code>action()</code> method
 * synchronizes on the object that will perform the action. Suppose the
 * <code>action()</code> method blocks because some other thread is already
 * executing a synchronized method on this object. Suppose the other thread
 * cancels the timer. Here is the race condition: the timer was canceled just as
 * it was triggered but before it could do the timed actions. When the other
 * thread returns, the <code>action()</code> method unblocks and proceeds to
 * execute. The <code>action()</code> method must check whether the timer got
 * canceled between the time when the <code>action()</code> method was called and
 * the time when the <code>action()</code> method started executing. If the
 * <code>action()</code> method doesn't do this check, it may erroneously perform
 * the timeout actions despite the timer cancellation.
 * <P>
 * Classes {@linkplain Timer}, TimerTask, and {@linkplain TimerThread} provide
 * capabilities similar to classes java.util.Timer and java.util.TimerTask.
 * Unlike the latter, they also provide the ability to stop and restart a timer
 * and the ability to deal with race conditions in multithreaded programs.
 *
 * @author Alan Kaminsky
 * @version 27-Sep-2002
 */
public interface TimerTask {

// Exported operations.
    /**
     * Perform this timer task's timed actions. The {@linkplain Timer} that was
     * triggered is passed in as an argument.
     * <P>
     * The <code>action()</code> method must check whether the timer is still
     * triggered. If it is, the <code>action()</code> method can perform its
     * processing. But if the timer is no longer triggered, the
     * <code>action()</code> method must return without doing anything.
     *
     * @param theTimer Timer that was triggered.
     */
    public void action(Timer theTimer);

}
