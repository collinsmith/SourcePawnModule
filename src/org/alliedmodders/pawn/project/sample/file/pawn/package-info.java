@TemplateRegistrations(value = {
    @TemplateRegistration(
	folder = "SourcePawn",
	displayName = "#emptyPawnScript_displayName",
	description = "./emptyPawnScript/emptyPawnScript.html",
	content = "./emptyPawnScript/emptyPawnScript.sp",
	scriptEngine = "freemarker"),
    @TemplateRegistration(
	folder = "SourcePawn",
	displayName = "#pawnScript_displayName",
	description = "./pawnScript/pawnScript.html",
	content = "./pawnScript/pawnScript.sp",
	scriptEngine = "freemarker"),
    @TemplateRegistration(
	folder = "SourcePawn",
	displayName = "#emptyIncludeFile_displayName",
	description = "./emptyIncludeFile/emptyIncludeFile.html",
	content = "./emptyIncludeFile/emptyIncludeFile.inc",
	scriptEngine = "freemarker"),
    @TemplateRegistration(
	folder = "SourcePawn",
	displayName = "#includeFile_displayName",
	description = "./includeFile/includeFile.html",
	content = "./includeFile/includeFile.inc",
	scriptEngine = "freemarker"),
    @TemplateRegistration(
	folder = "SourcePawn",
	displayName = "#emptyTestSuite_displayName",
	description = "./emptyTestSuite/emptyTestSuite.html",
	content = "./emptyTestSuite/emptyTestSuite.sp",
	scriptEngine = "freemarker"),
})
package org.alliedmodders.pawn.project.sample.file.pawn;

import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
