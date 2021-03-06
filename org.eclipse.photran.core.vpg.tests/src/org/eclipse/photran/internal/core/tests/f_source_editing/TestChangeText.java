package org.eclipse.photran.internal.core.tests.f_source_editing;

import org.eclipse.photran.internal.core.lexer.Token;
import org.eclipse.photran.internal.core.parser.ASTExecutableProgramNode;
import org.eclipse.photran.internal.core.parser.ASTMainProgramNode;
import org.eclipse.photran.internal.core.parser.ASTProgramNameNode;
import org.eclipse.photran.internal.core.parser.ASTProgramStmtNode;
import org.eclipse.photran.internal.core.parser.IProgramUnit;
import org.eclipse.photran.internal.core.tests.AbstractSourceEditorTestCase;

public class TestChangeText extends AbstractSourceEditorTestCase
{
    public void testChangeProggie() throws Exception
    {
        ASTExecutableProgramNode ast = load("hello-1-initial.f90");
        IProgramUnit programUnit1 = ast.getProgramUnitList().get(0);
        ASTMainProgramNode mainProgram = (ASTMainProgramNode)programUnit1;
        ASTProgramStmtNode programStmt = mainProgram.getProgramStmt();
        ASTProgramNameNode programName = programStmt.getProgramName();
        Token proggie = programName.getProgramName();
        
        proggie.setText("p");
        assertEquals(load("hello-2-proggie-shorter.f90"), ast);
        
        proggie.setText("proggiedoggie");
        assertEquals(load("hello-3-proggie-longer.f90"), ast);
    }
}
