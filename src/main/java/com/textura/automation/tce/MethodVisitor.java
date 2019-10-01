package com.textura.automation.tce;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodVisitor extends VoidVisitorAdapter<Void> {

	@Override
	public void visit(MethodDeclaration n, Void arg) {
		String containingStmt = "selenium().SendToSigner.clickSendToSigner();";
		String addStmt = "selenium().SendToSigner.clickSendToSignerModal();";

		n = TestCaseEditor.addStep(n, containingStmt, addStmt);
		// n = TestCaseEditor.removeStep(n, addStmt);
		super.visit(n, arg);
	}
}
