/*******************************************************************************
 * Copyright (c) 2000, 2006 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.make.internal.core.makefile.gnu;

import org.eclipse.cdt.make.internal.core.makefile.Directive;


public class Ifneq extends Conditional {

	public Ifneq(Directive parent, String cond) {
		super(parent, cond);
	}

	public boolean isIfneq() {
		return true;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(GNUMakefileConstants.CONDITIONAL_IFNEQ);
		sb.append(' ').append(getConditional());
		return sb.toString();
	}

}
