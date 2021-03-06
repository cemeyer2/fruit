/*******************************************************************************
 * Copyright (c) 2008 Institute for Software, HSR Hochschule fuer Technik  
 * Rapperswil, University of applied sciences and others
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *  
 * Contributors: 
 * Institute for Software - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.ui.refactoring.extractfunction;

import java.util.List;

import org.eclipse.text.edits.TextEditGroup;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFieldReference;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLiteralExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReturnStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPBasicType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTypedef;

import org.eclipse.cdt.internal.ui.refactoring.NodeContainer.NameInformation;

/**
 * Handles the extraction of expression nodes, like return type determination.
 * 
 * @author Mirko Stocker
 * 
 */
public class ExtractExpression extends ExtractedFunctionConstructionHelper {

	@Override
	public void constructMethodBody(IASTCompoundStatement compound,
			List<IASTNode> list, ASTRewrite rewrite, TextEditGroup group) {

		CPPASTReturnStatement statement = new CPPASTReturnStatement();
		IASTExpression nullReturnExp = new CPPASTLiteralExpression(IASTLiteralExpression.lk_integer_constant, "0"); //$NON-NLS-1$
		statement.setReturnValue(nullReturnExp);
		ASTRewrite nestedRewrite = rewrite.insertBefore(compound, null, statement, group);
		
		nestedRewrite.replace(nullReturnExp, list.get(0), group);
		
	}

	@Override
	public IASTDeclSpecifier determineReturnType(IASTNode extractedNode, NameInformation _) {
		IASTDeclSpecifier declSpecifier = null;
		
		if (extractedNode instanceof ICPPASTBinaryExpression) {
			declSpecifier = handleBinaryExpression((ICPPASTBinaryExpression) extractedNode);
		}
		
		if (extractedNode instanceof ICPPASTNewExpression) {
			declSpecifier = handleNewExpression((ICPPASTNewExpression) extractedNode);
		}

		if (extractedNode instanceof IASTFunctionCallExpression) {
			declSpecifier = handleFunctionCallExpression((IASTFunctionCallExpression) extractedNode);
		}
		
		if(declSpecifier == null) {
			return createSimpleDeclSpecifier(IASTSimpleDeclSpecifier.t_void);
		}
		
		return declSpecifier;
	}

	private IASTDeclSpecifier handleNewExpression(ICPPASTNewExpression expression) {
		return expression.getTypeId().getDeclSpecifier();
	}

	private IASTDeclSpecifier handleBinaryExpression(ICPPASTBinaryExpression node) {
		
		switch (node.getOperator()) {
		case IASTBinaryExpression.op_equals:
		case IASTBinaryExpression.op_notequals:
		case IASTBinaryExpression.op_logicalOr:
		case IASTBinaryExpression.op_logicalAnd:
		case IASTBinaryExpression.op_greaterEqual:
		case IASTBinaryExpression.op_greaterThan:
		case IASTBinaryExpression.op_lessEqual:
		case IASTBinaryExpression.op_lessThan:
		
			/* We assume that these operations evaluate to bool and don't 
			 * consider overriden operators from custom types for now.*/
			return createSimpleDeclSpecifier(ICPPASTSimpleDeclSpecifier.t_bool);
			
		case IASTBinaryExpression.op_plus:
		case IASTBinaryExpression.op_plusAssign:
		case IASTBinaryExpression.op_minus:
		case IASTBinaryExpression.op_minusAssign:
		case IASTBinaryExpression.op_multiply:
		case IASTBinaryExpression.op_multiplyAssign:
		case IASTBinaryExpression.op_divide:
		case IASTBinaryExpression.op_divideAssign:
		case IASTBinaryExpression.op_assign:
			
			/* Assume that the expression's return type is the same as the left operand's.*/
			
			if(node.getOperand1() instanceof CPPASTIdExpression) {
				IType expressionType = ((CPPASTIdExpression) node.getOperand1()).getExpressionType();
				
				if (expressionType instanceof CPPBasicType) {
					
					CPPBasicType basicType = (CPPBasicType) expressionType;
					return createSimpleDeclSpecifier(basicType.getType());
					
				} else if (expressionType instanceof CPPTypedef) {
					
					CPPTypedef typedef = (CPPTypedef) expressionType;
					return new CPPASTNamedTypeSpecifier((IASTName) typedef.getDefinition(), false);
					
				} else if (expressionType instanceof CPPClassType) {
					
					CPPClassType classType = (CPPClassType) expressionType;
					return new CPPASTNamedTypeSpecifier((IASTName) classType.getDefinition(), false);
				}
			}
		}
		
		return null /* not yet handled */;
	}
	
	private static IASTDeclSpecifier createSimpleDeclSpecifier(int type) {
		IASTSimpleDeclSpecifier declSpec = new CPPASTSimpleDeclSpecifier();
		declSpec.setType(type);
		return declSpec;
	}
	
	private static CPPFunction findCalledFunction(IASTFunctionCallExpression callExpression) {
		IASTExpression functionNameExpression = callExpression.getFunctionNameExpression();
		IASTName functionName = null;
		
		if(functionNameExpression instanceof CPPASTIdExpression) {
			CPPASTIdExpression idExpression = (CPPASTIdExpression) functionNameExpression;
			functionName = idExpression.getName();
		} else  if(functionNameExpression instanceof CPPASTFieldReference) {
			CPPASTFieldReference fieldReference = (CPPASTFieldReference) functionNameExpression;
			functionName = fieldReference.getFieldName();
		} else {
			return null;
		}
		
		if (functionName.resolveBinding() instanceof CPPFunction) {
			return (CPPFunction) functionName.resolveBinding();
		}
		
		return null;
	}
	
	private static IASTDeclSpecifier handleFunctionCallExpression(IASTFunctionCallExpression callExpression) {
		CPPFunction function = findCalledFunction(callExpression);
		
		if (function != null) {
			if(function.getDefinition() != null) {
				IASTNode parent = function.getDefinition().getParent();
				if(parent instanceof CPPASTFunctionDefinition) {
					CPPASTFunctionDefinition definition = (CPPASTFunctionDefinition) parent;
					return definition.getDeclSpecifier();
				}
			} else if(hasDeclaration(function)) {
				IASTNode parent = function.getDeclarations()[0].getParent();
				if (parent instanceof CPPASTSimpleDeclaration) {
					CPPASTSimpleDeclaration declaration = (CPPASTSimpleDeclaration) parent;
					return declaration.getDeclSpecifier();
				}
			}
		}
			
		return null;
	}
	
	@Override
	protected boolean isReturnTypeAPointer(IASTNode node) {
		if(node instanceof ICPPASTNewExpression) {
			return true;
		} else if(!(node instanceof IASTFunctionCallExpression)) {
			return false;
		}
		
		CPPFunction function = findCalledFunction((IASTFunctionCallExpression) node);
		
		if (function != null) {
			if(function.getDefinition() != null) {
				IASTNode parent = function.getDefinition().getParent();
				if(parent instanceof CPPASTFunctionDefinition) {
					CPPASTFunctionDefinition definition = (CPPASTFunctionDefinition) parent;
					return definition.getDeclarator().getPointerOperators().length > 0;
				}
			} else if(hasDeclaration(function)) {
				IASTNode parent = function.getDeclarations()[0].getParent();
				if (parent instanceof CPPASTSimpleDeclaration) {
					CPPASTSimpleDeclaration declaration = (CPPASTSimpleDeclaration) parent;
					return declaration.getDeclarators().length > 0 && declaration.getDeclarators()[0].getPointerOperators().length > 0;
				}
			}
		}
			
		return false;
	}

	private static boolean hasDeclaration(CPPFunction function) {
		return function != null && function.getDeclarations() != null && function.getDeclarations().length > 0;
	}
	
	@Override
	public IASTNode createReturnAssignment(IASTNode node, IASTExpressionStatement stmt, IASTExpression callExpression) {
		return callExpression;
	}
}
