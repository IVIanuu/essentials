/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.kotlin.compiler.compose.ast

import org.jetbrains.kotlin.com.intellij.psi.PsiElement

const val COMMAND_PREFIX = "//meta"

sealed class Node {
    var tag: Any? = null
    var dynamic: String? = null
    var element: PsiElement? = null
    var parent: Node? = null
    val extras = mutableMapOf<Any, Any?>()

    interface WithAnnotations {
        val anns: List<Modifier.AnnotationSet>
    }

    interface WithModifiers : WithAnnotations {
        var mods: List<Modifier>
        override val anns: List<Modifier.AnnotationSet>
            get() = mods.mapNotNull { it as? Modifier.AnnotationSet }
    }

    interface Entry : WithAnnotations {
        var pkg: Package?
        var imports: List<Import>
    }

    data class File(
        override var anns: List<Modifier.AnnotationSet>,
        override var pkg: Package?,
        override var imports: List<Import>,
        var commands: List<Command>,
        var decls: List<Decl>
    ) : Node(), Entry

    data class Script(
        override var anns: List<Modifier.AnnotationSet>,
        override var pkg: Package?,
        override var imports: List<Import>,
        var exprs: List<Expr>
    ) : Node(), Entry

    data class Package(
        override var mods: List<Modifier>,
        var names: List<String>
    ) : Node(), WithModifiers

    data class Import(
        var names: List<String>,
        var wildcard: Boolean,
        var alias: String?
    ) : Node()

    data class Command(
        var name: String
    ) : Node()

    sealed class Decl : Node() {
        data class Structured(
            override var mods: List<Modifier>,
            var form: Form,
            var name: String,
            var typeParams: List<TypeParam>,
            var primaryConstructor: PrimaryConstructor?,
            var parentAnns: List<Modifier.AnnotationSet>,
            var parents: List<Parent>,
            var typeConstraints: List<TypeConstraint>,
            // TODO: Can include primary constructor
            var members: List<Decl>
        ) : Decl(), WithModifiers {
            enum class Form {
                CLASS, ENUM_CLASS, INTERFACE, OBJECT, COMPANION_OBJECT
            }

            sealed class Parent : Node() {
                data class CallConstructor(
                    var type: TypeRef.Simple,
                    var typeArgs: List<Node.Type?>,
                    var args: List<ValueArg>,
                    var lambda: Expr.Call.TrailLambda?
                ) : Parent()

                data class Type(
                    var type: TypeRef,
                    var by: Expr?
                ) : Parent()
            }

            data class PrimaryConstructor(
                override var mods: List<Modifier>,
                var params: List<Func.Param>
            ) : Node(), WithModifiers
        }

        data class Init(var block: Block) : Decl()
        data class Func(
            override var mods: List<Modifier>,
            var typeParams: List<TypeParam>,
            var receiverType: Type?,
            // Name not present on anonymous functions
            var name: String?,
            var paramTypeParams: List<TypeParam>,
            var params: List<Func.Param>,
            var type: Type?,
            var typeConstraints: List<TypeConstraint>,
            var body: Body?
        ) : Decl(), WithModifiers {
            data class Param(
                override var mods: List<Modifier>,
                var readOnly: Boolean?,
                var name: String,
                // Type can be null for anon functions
                var type: Type?,
                var default: Expr?
            ) : Node(), WithModifiers

            sealed class Body : Node() {
                data class Block(var block: Node.Block) : Body()
                data class Expr(var expr: Node.Expr) : Body()
            }
        }

        data class Property(
            override var mods: List<Modifier>,
            var readOnly: Boolean,
            var typeParams: List<TypeParam>,
            var receiverType: Type?,
            // Always at least one, more than one is destructuring, null is underscore in destructure
            var vars: List<Var?>,
            var typeConstraints: List<TypeConstraint>,
            var delegated: Boolean,
            var expr: Expr?,
            var accessors: Accessors?
        ) : Decl(), WithModifiers {
            data class Var(
                var name: String,
                var type: Type?
            ) : Node()

            data class Accessors(
                var first: Accessor,
                var second: Accessor?
            ) : Node()

            sealed class Accessor : Node(), WithModifiers {
                data class Get(
                    override var mods: List<Modifier>,
                    var type: Type?,
                    var body: Func.Body?
                ) : Accessor()

                data class Set(
                    override var mods: List<Modifier>,
                    var paramMods: List<Modifier>,
                    var paramName: String?,
                    var paramType: Type?,
                    var body: Func.Body?
                ) : Accessor()
            }
        }

        data class TypeAlias(
            override var mods: List<Modifier>,
            var name: String,
            var typeParams: List<TypeParam>,
            var type: Type
        ) : Decl(), WithModifiers

        data class Constructor(
            override var mods: List<Modifier>,
            var params: List<Func.Param>,
            var delegationCall: DelegationCall?,
            var block: Block?
        ) : Decl(), WithModifiers {
            data class DelegationCall(
                var target: DelegationTarget,
                var args: List<ValueArg>
            ) : Node()

            enum class DelegationTarget { THIS, SUPER }
        }

        data class EnumEntry(
            override var mods: List<Modifier>,
            var name: String,
            var args: List<ValueArg>,
            var members: List<Decl>
        ) : Decl(), WithModifiers
    }

    data class TypeParam(
        override var mods: List<Modifier>,
        var name: String,
        var type: TypeRef?
    ) : Node(), WithModifiers

    data class TypeConstraint(
        override var anns: List<Modifier.AnnotationSet>,
        var name: String,
        var type: Type
    ) : Node(), WithAnnotations

    sealed class TypeRef : Node() {
        data class Paren(
            override var mods: List<Modifier>,
            var type: TypeRef
        ) : TypeRef(), WithModifiers

        data class Func(
            var receiverType: Type?,
            var params: List<Param>,
            var type: Type
        ) : TypeRef() {
            data class Param(
                var name: String?,
                var type: Type
            ) : Node()
        }

        data class Simple(
            var pieces: List<Piece>
        ) : TypeRef() {
            data class Piece(
                var name: String,
                // Null means any
                var typeParams: List<Type?>
            ) : Node()
        }

        data class Nullable(var type: TypeRef) : TypeRef()
        data class Dynamic(var _unused_: Boolean = false) : TypeRef()
    }

    data class Type(
        override var mods: List<Modifier>,
        var ref: TypeRef
    ) : Node(), WithModifiers

    data class ValueArg(
        var name: String?,
        var asterisk: Boolean,
        var expr: Expr
    ) : Node()

    sealed class Expr : Node() {
        data class If(
            var expr: Expr,
            var body: Expr,
            var elseBody: Expr?
        ) : Expr()

        data class Try(
            var block: Block,
            var catches: List<Catch>,
            var finallyBlock: Block?
        ) : Expr() {
            data class Catch(
                override var anns: List<Modifier.AnnotationSet>,
                var varName: String,
                var varType: TypeRef.Simple,
                var block: Block
            ) : Node(), WithAnnotations
        }

        data class For(
            override var anns: List<Modifier.AnnotationSet>,
            // More than one means destructure, null means underscore
            var vars: List<Decl.Property.Var?>,
            var inExpr: Expr,
            var body: Expr
        ) : Expr(), WithAnnotations

        data class While(
            var expr: Expr,
            var body: Expr,
            var doWhile: Boolean
        ) : Expr()

        data class BinaryOp(
            var lhs: Expr,
            var oper: Oper,
            var rhs: Expr
        ) : Expr() {
            sealed class Oper : Node() {
                data class Infix(var str: String) : Oper()
                data class Token(var token: BinaryOp.Token) : Oper()
            }

            enum class Token(var str: String) {
                MUL("*"), DIV("/"), MOD("%"), ADD("+"), SUB("-"),
                IN("in"), NOT_IN("!in"),
                GT(">"), GTE(">="), LT("<"), LTE("<="),
                EQ("=="), NEQ("!="),
                ASSN("="), MUL_ASSN("*="), DIV_ASSN("/="), MOD_ASSN("%="), ADD_ASSN("+="), SUB_ASSN(
                    "-="
                ),
                OR("||"), AND("&&"), ELVIS("?:"), RANGE(".."),
                DOT("."), DOT_SAFE("?."), SAFE("?")
            }
        }

        data class UnaryOp(
            var expr: Expr,
            var oper: Oper,
            var prefix: Boolean
        ) : Expr() {
            data class Oper(var token: Token) : Node()
            enum class Token(var str: String) {
                NEG("-"), POS("+"), INC("++"), DEC("--"), NOT("!"), NULL_DEREF("!!")
            }
        }

        data class TypeOp(
            var lhs: Expr,
            var oper: Oper,
            var rhs: Type
        ) : Expr() {
            data class Oper(var token: Token) : Node()
            enum class Token(var str: String) {
                AS("as"), AS_SAFE("as?"), COL(":"), IS("is"), NOT_IS("!is")
            }
        }

        sealed class DoubleColonRef : Expr() {
            abstract var recv: Recv?

            data class Callable(
                override var recv: Recv?,
                var name: String
            ) : DoubleColonRef()

            data class Class(
                override var recv: Recv?
            ) : DoubleColonRef()

            sealed class Recv : Node() {
                data class Expr(var expr: Node.Expr) : Recv()
                data class Type(
                    var type: TypeRef.Simple,
                    var questionMarks: Int
                ) : Recv()
            }
        }

        data class Paren(
            var expr: Expr
        ) : Expr()

        data class StringTmpl(
            var elems: List<Elem>,
            var raw: Boolean
        ) : Expr() {
            sealed class Elem : Node() {
                data class Regular(var str: String) : Elem()
                data class ShortTmpl(var expr: Expr) : Elem()
                data class UnicodeEsc(var digits: String) : Elem()
                data class RegularEsc(var char: Char) : Elem()
                data class LongTmpl(var expr: Expr) : Elem()
            }
        }

        data class Const(
            var value: String,
            var form: Form
        ) : Expr() {
            enum class Form { BOOLEAN, CHAR, INT, FLOAT, NULL }
        }

        data class Brace(
            var params: List<Param>,
            var block: Block?
        ) : Expr() {
            data class Param(
                // Multiple means destructure, null means underscore
                var vars: List<Decl.Property.Var?>,
                var destructType: Type?
            ) : Expr()
        }

        data class This(
            var label: String?
        ) : Expr()

        data class Super(
            var typeArg: Type?,
            var label: String?
        ) : Expr()

        data class When(
            var expr: Expr?,
            var entries: List<Entry>
        ) : Expr() {
            data class Entry(
                var conds: List<Cond>,
                var body: Expr
            ) : Node()

            sealed class Cond : Node() {
                data class Expr(var expr: Node.Expr) : Cond()
                data class In(
                    var expr: Node.Expr,
                    var not: Boolean
                ) : Cond()

                data class Is(
                    var type: Type,
                    var not: Boolean
                ) : Cond()
            }
        }

        data class Object(
            var parents: List<Decl.Structured.Parent>,
            var members: List<Decl>
        ) : Expr()

        data class Throw(
            var expr: Expr
        ) : Expr()

        data class Return(
            var label: String?,
            var expr: Expr?
        ) : Expr()

        data class Continue(
            var label: String?
        ) : Expr()

        data class Break(
            var label: String?
        ) : Expr()

        data class CollLit(
            var exprs: List<Expr>
        ) : Expr()

        data class Name(var name: String) : Expr()

        data class Labeled(
            var label: String,
            var expr: Expr
        ) : Expr()

        data class Annotated(
            override var anns: List<Modifier.AnnotationSet>,
            var expr: Expr
        ) : Expr(), WithAnnotations

        data class Call(
            var expr: Expr,
            var typeArgs: List<Type?>,
            var args: List<ValueArg>,
            var lambda: TrailLambda?
        ) : Expr() {
            data class TrailLambda(
                override var anns: List<Modifier.AnnotationSet>,
                var label: String?,
                var func: Brace
            ) : Node(), WithAnnotations
        }

        data class ArrayAccess(
            var expr: Expr,
            var indices: List<Expr>
        ) : Expr()

        data class AnonFunc(
            var func: Decl.Func
        ) : Expr()

        // This is only present for when expressions and labeled expressions
        data class Property(
            var decl: Decl.Property
        ) : Expr()
    }

    data class Block(var stmts: List<Stmt>) : Node()
    sealed class Stmt : Node() {
        data class Decl(var decl: Node.Decl) : Stmt()
        data class Expr(var expr: Node.Expr) : Stmt()
    }

    sealed class Modifier : Node() {
        data class AnnotationSet(
            var target: Target?,
            var anns: List<Annotation>
        ) : Modifier() {
            enum class Target {
                FIELD, FILE, PROPERTY, GET, SET, RECEIVER, PARAM, SETPARAM, DELEGATE
            }

            data class Annotation(
                var names: List<String>,
                var typeArgs: List<Type>,
                var args: List<ValueArg>
            ) : Node()
        }

        data class Lit(var keyword: Keyword) : Modifier()
        enum class Keyword {
            ABSTRACT, FINAL, OPEN, ANNOTATION, SEALED, DATA, OVERRIDE, LATEINIT, INNER,
            PRIVATE, PROTECTED, PUBLIC, INTERNAL,
            IN, OUT, NOINLINE, CROSSINLINE, VARARG, REIFIED,
            TAILREC, OPERATOR, INFIX, INLINE, EXTERNAL, SUSPEND, CONST,
            ACTUAL, EXPECT
        }
    }

    sealed class Extra : Node() {
        data class BlankLines(
            var count: Int
        ) : Extra()

        data class Comment(
            var text: String,
            var startsLine: Boolean,
            var endsLine: Boolean
        ) : Extra()
    }
}
