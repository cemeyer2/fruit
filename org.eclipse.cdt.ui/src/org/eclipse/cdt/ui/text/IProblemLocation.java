/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.ui.text;

/**
 * Problem information for quick fix and quick assist processors.
 */
public interface IProblemLocation {
	/**
	 * Returns the start offset of the problem.
	 *
	 * @return the start offset of the problem
	 */
	int getOffset();

	/**
	 * Returns the length of the problem.
	 *
	 * @return the length of the problem
	 */
	int getLength();

	/**
	 * Returns the marker type of this problem.
	 *
	 * @return The marker type of the problem.
	 */
	String getMarkerType();
	
	/**
	 * Returns the id of problem. Note that problem ids are defined per problem marker type.
	 *
	 * @return The id of the problem.
	 */
	int getProblemId();

	/**
	 * Returns the original arguments recorded into the problem.
	 *
	 * @return String[] Returns the problem arguments.
	 */
	String[] getProblemArguments();

	/**
	 * Returns if the problem has error severity.
	 *
	 * @return <code>true</code> if the problem has error severity
	 */
	boolean isError();
}
