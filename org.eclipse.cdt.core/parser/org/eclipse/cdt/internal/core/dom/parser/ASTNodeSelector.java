/*******************************************************************************
 * Copyright (c) 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Schorn - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.cdt.internal.core.dom.parser;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;
import org.eclipse.cdt.internal.core.dom.parser.ASTNodeSpecification.Relation;
import org.eclipse.cdt.internal.core.parser.scanner.ILocationResolver;

/**
 * Class to support searching for nodes by file offsets.
 * @since 5.0
 */
public class ASTNodeSelector implements IASTNodeSelector {

	private ASTTranslationUnit fTu;
	private ILocationResolver fLocationResolver;
	private String fFilePath;
	private final boolean fIsValid;

	public ASTNodeSelector(ASTTranslationUnit tu, ILocationResolver locationResolver, String filePath) {
		fTu= tu;
		fLocationResolver= locationResolver;
		fFilePath= filePath;
		fIsValid= verify();
	}

	private boolean verify() {
		if (fLocationResolver != null) {
			if (fFilePath == null) {
				fFilePath= fLocationResolver.getTranslationUnitPath();
			}
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.IASTNodeSelector#getNode(int, int)
	 */
	private <T extends IASTNode> T findNode(int offsetInFile, int lengthInFile, Relation relation, Class<T> requiredClass) {
		if (!fIsValid) {
			return null;
		}
		if (lengthInFile < 0) {
			throw new IllegalArgumentException("Length cannot be less than zero."); //$NON-NLS-1$
		}
		int sequenceLength;
		int altSequenceNumber= -1;
		int sequenceNumber= fLocationResolver.getSequenceNumberForFileOffset(fFilePath, offsetInFile);
    	if (sequenceNumber < 0) {
    		return null;
    	}
		if (lengthInFile > 0) {
			sequenceLength= fLocationResolver.getSequenceNumberForFileOffset(fFilePath, offsetInFile+lengthInFile-1) + 1 - sequenceNumber;
		}
		else {
			sequenceLength= 0;
			if (offsetInFile > 0) {
				altSequenceNumber= fLocationResolver.getSequenceNumberForFileOffset(fFilePath, offsetInFile-1);
				if (altSequenceNumber+1 == sequenceNumber) {
					altSequenceNumber= -1;
				}
				else {
					// we are on a context boundary and we need to check the variant to the left and
					// the one to the right
 					sequenceLength= 1;
				}
			}
		}
		final ASTNodeSpecification<T> nodeSpec= new ASTNodeSpecification<T>(relation, requiredClass, offsetInFile, lengthInFile);
		nodeSpec.setRangeInSequence(sequenceNumber, sequenceLength);
    	getNode(nodeSpec);
    	if (altSequenceNumber != -1) {
    		nodeSpec.setRangeInSequence(altSequenceNumber, sequenceLength);
        	getNode(nodeSpec);
    	}
    	return nodeSpec.getBestNode();
	}

	private <T extends IASTNode> T getNode(ASTNodeSpecification<T> nodeSpec) {
		fLocationResolver.findPreprocessorNode(nodeSpec);
    	if (!nodeSpec.requiresClass(IASTPreprocessorMacroExpansion.class)) {
    		FindNodeForOffsetAction nodeFinder= new FindNodeForOffsetAction(nodeSpec);
    		fTu.accept(nodeFinder);
    	}
		return nodeSpec.getBestNode();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.IASTNodeSelector#getFirstContainedNode(int, int)
	 */
	public IASTNode findFirstContainedNode(int offset, int length) {
		return findNode(offset, length, Relation.FIRST_CONTAINED, IASTNode.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.IASTNodeSelector#getNode(int, int)
	 */
	public IASTNode findNode(int offset, int length) {
		return findNode(offset, length, Relation.EXACT_MATCH, IASTNode.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.IASTNodeSelector#getSurroundingNode(int, int)
	 */
	public IASTNode findEnclosingNode(int offset, int length) {
		return findNode(offset, length, Relation.ENCLOSING, IASTNode.class);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.IASTNodeSelector#getFirstContainedNode(int, int)
	 */
	public IASTName findFirstContainedName(int offset, int length) {
		return findNode(offset, length, Relation.FIRST_CONTAINED, IASTName.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.IASTNodeSelector#getNode(int, int)
	 */
	public IASTName findName(int offset, int length) {
		return findNode(offset, length, Relation.EXACT_MATCH, IASTName.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.IASTNodeSelector#getSurroundingNode(int, int)
	 */
	public IASTName findEnclosingName(int offset, int length) {
		return findNode(offset, length, Relation.ENCLOSING, IASTName.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.dom.ast.IASTNodeSelector#findSurrundingMacroExpansion(int, int)
	 */
	public IASTPreprocessorMacroExpansion findEnclosingMacroExpansion(int offset, int length) {
		return findNode(offset, length, Relation.ENCLOSING, IASTPreprocessorMacroExpansion.class);
	}
 
}