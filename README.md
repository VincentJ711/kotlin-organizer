# kotlin-organizer
sorts your kotlin functions either by dfs or bfs

# TODO

enum class DeclSection:
    Setup == props,init blocks, 2ndary ctors
    Funcs
    Enums
    Classes
    Companions
    Objects
    Other -> any decl thats not one of the above.
    
fun sort(ctr: PsiElement, decls: List<KTDecl>):
    val sections = linkedMapOf<DeclSection, LinkedSet<KtDecl>>()
    var orderedFuncs = sections[DeclSection.Funcs]
    
    initialize each DeclSection in sections to an empty set
    
    decls.forEach
        if decl is KtClassOrObject
            sort(decl, decl.declarations)
        sections[getSection(decl)].add(decl)
    
    if bfs or dfs is set:
        let declDeps = linkedMapOf<KTDecl, LinkedSet<KtFunction>>
        
        decls.forEach
            if decl is a function or 
                    (setupSectionAboveFunctionSection &&
                    includeSetupSectionForGraphSearch &&
                    (decl is prop or ctor or init block)):
                val deps = linkedSetOf<KtFunction>
                decl.accept(KtDeclWalker(decl, decls, deps)
                declDeps[decl] = deps
        
        orderedFuncs = orderFuncsByDeps(declDeps) 
        # does dfs/bfs starting w/ first key until all visited
    
    else if orderFuncsAlphabetically:
        orderedFuncs = orderedFuncs.sortBy { it.name }
           
    else if orderFuncsByModifiers:
        orderedFuncs = orderFuncsByModifiers(orderedFuncs)
 
    sections[DeclSection.Funcs] = orderedFuncs
    
    val before = decls.toList()
    val after = flattenToDecls(sections)
    
    # following is same as before.
    if (before != after && valid)
        ...