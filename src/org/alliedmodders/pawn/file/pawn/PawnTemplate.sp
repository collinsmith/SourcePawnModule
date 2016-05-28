#include <sourcemod>
#pragma dynamic 256
#if defined _included
    #endinput
#endif

public Plugin pluginInfo = {
    name = "Sample",
    author = "John Smith",
    description = "A sample sourcemod plugin",
    version = "1.0.0",
    url = "http://www.sourcemod.net/"
};

// Single line comment
char g_szHelloWorld[] = "Hello World";

public void OnPluginStart() {
    PrintHelloWorld(0);
    new iValue = 1;
    new Float:fValue = 1.0;
    new cValue = '1';
}

/**
 * Some documentation with an <i>HTML</i> tag
 *
 * @param id    an identifier
 */
void PrintHelloWorld(number) {
    PrintToServer(g_szHelloWorld);
}