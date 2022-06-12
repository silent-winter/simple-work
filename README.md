# 编译原理大作业
我们小组只实现了词法分析+语法分析部分，输入源程序，可输出词法分析过程，语法分析过程。
### 项目目录结构介绍
```text
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─xinzi
│  │  │          ├─compile
│  │  │          │  │  Application.java //主程序入口
│  │  │          │  │
│  │  │          │  ├─lexical   //词法分析程序
│  │  │          │  │  │  LexicalAnalyzer.java
│  │  │          │  │  │  RegKeyEnum.java
│  │  │          │  │  │  State.java
│  │  │          │  │  │
│  │  │          │  │  ├─dfa    //构建dfa相关
│  │  │          │  │  │      DFA.java
│  │  │          │  │  │      DFAState.java
│  │  │          │  │  │      MinDFAState.java
│  │  │          │  │  │
│  │  │          │  │  ├─nfa    //构建nfa相关
│  │  │          │  │  │      NFA.java
│  │  │          │  │  │      NFACouple.java
│  │  │          │  │  │      NFAState.java
│  │  │          │  │  │
│  │  │          │  │  └─util
│  │  │          │  │          FileUtil.java
│  │  │          │  │          OperationUtil.java
│  │  │          │  │
│  │  │          │  └─syntactic     //语法分析程序
│  │  │          │          ExpressionUtil.java
│  │  │          │          PredictiveAnalyzer.java
│  │  │          │          SyntacticAnalyzer.java
│  │  └─resources
│  │          grammer.properties    //正则表达式语法配置
│  │          source_char.txt       //源程序文件
```
###LL(1)文法设计
为何条件判断只能A>A的形式，最初是打算设计D -> A>A|A<A|A==A|A!=A，但不满足LL(1)文法，而本程序基于LL(1)实现，所以暂未想到更好的解法
```text
E → main()T
T → {F}
F → G | B | C
G → i=A;V
V → F | ε
A → SA'
A'→ +SA' | -SA' | ε
S → HS'
S' → *H | /H | ε
H → (S) | id | number
B → if(D)T
C → while(D)T
D → A>A
```
###正则表达式语法设计
由于本组设计的正则表达式语法'('和')'用于控制优先级，且暂不支持转义，所以对于token中含有'('的情况只能特殊处理。不支持[0-9][a-z]等形式，有点蠢。。。
```properties
number=(0|1|2|3|4|5|6|7|8|9)*
keyword=main|int|char|if|else|for|while|end
identifier=(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|_)(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|_|0|1|2|3|4|5|6|7|8|9)*
special=+|-|/|[|]|{|}|,|;|:|>|<|>=|<=|==|!=|=
```
###源程序示例
由于没法更好的确定分割符，所以只好在每个token前后加上空格
```text
main ( ) {
      a = 1 ;
      b = 2 ;
      c = 3 ;
      while ( a > 0 ) {
          a = a - 1 ;
          while ( b > 0 ) {
              b = b - 1 ;
              if ( c > 0 ) {
                  c = 0 ;
              }
          }
     }
}
```
###输出示例
```text
=====================词法分析结果=====================
token=keyword, value="main"
token=brackets, value="("
token=brackets, value=")"
token=special, value="{"
token=identifier, value="a"
token=special, value="="
token=number, value="1"
token=special, value=";"
token=identifier, value="b"
token=special, value="="
token=number, value="2"
token=special, value=";"
token=identifier, value="c"
token=special, value="="
token=number, value="3"
token=special, value=";"
token=keyword, value="while"
token=brackets, value="("
token=identifier, value="a"
token=special, value=">"
token=number, value="0"
token=brackets, value=")"
token=special, value="{"
token=identifier, value="a"
token=special, value="="
token=identifier, value="a"
token=special, value="-"
token=number, value="1"
token=special, value=";"
token=keyword, value="while"
token=brackets, value="("
token=identifier, value="b"
token=special, value=">"
token=number, value="0"
token=brackets, value=")"
token=special, value="{"
token=identifier, value="b"
token=special, value="="
token=identifier, value="b"
token=special, value="-"
token=number, value="1"
token=special, value=";"
token=keyword, value="if"
token=brackets, value="("
token=identifier, value="c"
token=special, value=">"
token=number, value="0"
token=brackets, value=")"
token=special, value="{"
token=identifier, value="c"
token=special, value="="
token=number, value="0"
token=special, value=";"
token=special, value="}"
token=special, value="}"
token=special, value="}"
===================first集==================
E's FirstSet = [m]
T's FirstSet = [{]
F's FirstSet = [f, w, i]
G's FirstSet = [i]
V's FirstSet = [ε, f, w, i]
A's FirstSet = [(, i, n]
Z's FirstSet = [ε, +, -]
S's FirstSet = [(, i, n]
X's FirstSet = [ε, *, /]
H's FirstSet = [(, i, n]
B's FirstSet = [f]
C's FirstSet = [w]
D's FirstSet = [(, i, n]
===================follow集==================
E's FollowSet = [$]
T's FollowSet = [$, }]
F's FollowSet = [}]
G's FollowSet = [}]
V's FollowSet = [}]
A's FollowSet = [), ;, >]
Z's FollowSet = [), ;, >]
S's FollowSet = [), +, ;, -, >]
X's FollowSet = [), +, ;, -, >]
H's FollowSet = [), *, +, ;, -, >, /]
B's FollowSet = [}]
C's FollowSet = [}]
D's FollowSet = [)]
===================select集==================
E->m()T's SelectSet = [m]
T->{F}'s SelectSet = [{]
F->B's SelectSet = [f]
F->C's SelectSet = [w]
F->G's SelectSet = [i]
G->i=A;V's SelectSet = [i]
V->ε's SelectSet = [}]
V->F's SelectSet = [f, w, i]
A->SZ's SelectSet = [(, i, n]
Z->+SZ's SelectSet = [+]
Z->-SZ's SelectSet = [-]
Z->ε's SelectSet = [), ;, >]
S->HX's SelectSet = [(, i, n]
X->/H's SelectSet = [/]
X->ε's SelectSet = [), +, ;, -, >]
X->*H's SelectSet = [*]
H->i's SelectSet = [i]
H->(S)'s SelectSet = [(]
H->n's SelectSet = [n]
B->f(D)T's SelectSet = [f]
C->w(D)T's SelectSet = [w]
D->A>A's SelectSet = [(, i, n]
===================预测分析表==================
E's PredictiveMap = {m=[m()T]}
T's PredictiveMap = {{=[{F}]}
F's PredictiveMap = {f=[B], w=[C], i=[G]}
G's PredictiveMap = {i=[i=A;V]}
V's PredictiveMap = {f=[F], w=[F], i=[F], }=[ε]}
A's PredictiveMap = {(=[SZ], i=[SZ], n=[SZ]}
Z's PredictiveMap = {)=[ε], ;=[ε], +=[+SZ], -=[-SZ], >=[ε]}
S's PredictiveMap = {(=[HX], i=[HX], n=[HX]}
X's PredictiveMap = {)=[ε], *=[*H], +=[ε], ;=[ε], -=[ε], >=[ε], /=[/H]}
H's PredictiveMap = {(=[(S)], i=[i], n=[n]}
B's PredictiveMap = {f=[f(D)T]}
C's PredictiveMap = {w=[w(D)T]}
D's PredictiveMap = {(=[A>A], i=[A>A], n=[A>A]}
=====================语法分析过程=====================
分析栈: [T, ), (, m]		剩余输入: main(){a=1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: E->m()T
分析栈: [T, ), (]		剩余输入: (){a=1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [T, )]		剩余输入: ){a=1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [T]		剩余输入: {a=1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, F, {]		剩余输入: {a=1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: T->{F}
分析栈: [}, F]		剩余输入: a=1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, G]		剩余输入: a=1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: F->G
分析栈: [}, V, ;, A, =, i]		剩余输入: a=1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: G->i=A;V
分析栈: [}, V, ;, A, =]		剩余输入: =1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, V, ;, A]		剩余输入: 1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, V, ;, Z, S]		剩余输入: 1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: A->SZ
分析栈: [}, V, ;, Z, X, H]		剩余输入: 1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: S->HX
分析栈: [}, V, ;, Z, X, n]		剩余输入: 1;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: H->n
分析栈: [}, V, ;, Z, X]		剩余输入: ;b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, V]		剩余输入: b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, F]		剩余输入: b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: V->F
分析栈: [}, G]		剩余输入: b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: F->G
分析栈: [}, V, ;, A, =, i]		剩余输入: b=2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: G->i=A;V
分析栈: [}, V, ;, A, =]		剩余输入: =2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, V, ;, A]		剩余输入: 2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, V, ;, Z, S]		剩余输入: 2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: A->SZ
分析栈: [}, V, ;, Z, X, H]		剩余输入: 2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: S->HX
分析栈: [}, V, ;, Z, X, n]		剩余输入: 2;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: H->n
分析栈: [}, V, ;, Z, X]		剩余输入: ;c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, V]		剩余输入: c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, F]		剩余输入: c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: V->F
分析栈: [}, G]		剩余输入: c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: F->G
分析栈: [}, V, ;, A, =, i]		剩余输入: c=3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: G->i=A;V
分析栈: [}, V, ;, A, =]		剩余输入: =3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, V, ;, A]		剩余输入: 3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, V, ;, Z, S]		剩余输入: 3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: A->SZ
分析栈: [}, V, ;, Z, X, H]		剩余输入: 3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: S->HX
分析栈: [}, V, ;, Z, X, n]		剩余输入: 3;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: H->n
分析栈: [}, V, ;, Z, X]		剩余输入: ;while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, V]		剩余输入: while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, F]		剩余输入: while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: V->F
分析栈: [}, C]		剩余输入: while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: F->C
分析栈: [}, T, ), D, (, w]		剩余输入: while(a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: C->w(D)T
分析栈: [}, T, ), D, (]		剩余输入: (a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, T, ), D]		剩余输入: a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, T, ), A, >, A]		剩余输入: a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: D->A>A
分析栈: [}, T, ), A, >, Z, S]		剩余输入: a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: A->SZ
分析栈: [}, T, ), A, >, Z, X, H]		剩余输入: a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: S->HX
分析栈: [}, T, ), A, >, Z, X, i]		剩余输入: a>0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: H->i
分析栈: [}, T, ), A, >, Z, X]		剩余输入: >0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, T, ), A]		剩余输入: 0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, T, ), Z, S]		剩余输入: 0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: A->SZ
分析栈: [}, T, ), Z, X, H]		剩余输入: 0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: S->HX
分析栈: [}, T, ), Z, X, n]		剩余输入: 0){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: H->n
分析栈: [}, T, ), Z, X]		剩余输入: ){a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, T]		剩余输入: {a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, F, {]		剩余输入: {a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: T->{F}
分析栈: [}, }, F]		剩余输入: a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, G]		剩余输入: a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: F->G
分析栈: [}, }, V, ;, A, =, i]		剩余输入: a=a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: G->i=A;V
分析栈: [}, }, V, ;, A, =]		剩余输入: =a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, V, ;, A]		剩余输入: a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, V, ;, Z, S]		剩余输入: a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: A->SZ
分析栈: [}, }, V, ;, Z, X, H]		剩余输入: a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: S->HX
分析栈: [}, }, V, ;, Z, X, i]		剩余输入: a-1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: H->i
分析栈: [}, }, V, ;, Z, X]		剩余输入: -1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, V, ;, Z, S, -]		剩余输入: -1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: Z->-SZ
分析栈: [}, }, V, ;, Z, S]		剩余输入: 1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, V, ;, Z, X, H]		剩余输入: 1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: S->HX
分析栈: [}, }, V, ;, Z, X, n]		剩余输入: 1;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: H->n
分析栈: [}, }, V, ;, Z, X]		剩余输入: ;while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, V]		剩余输入: while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, F]		剩余输入: while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: V->F
分析栈: [}, }, C]		剩余输入: while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: F->C
分析栈: [}, }, T, ), D, (, w]		剩余输入: while(b>0){b=b-1;if(c>0){c=0;}}}}		产生式: C->w(D)T
分析栈: [}, }, T, ), D, (]		剩余输入: (b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, T, ), D]		剩余输入: b>0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, T, ), A, >, A]		剩余输入: b>0){b=b-1;if(c>0){c=0;}}}}		产生式: D->A>A
分析栈: [}, }, T, ), A, >, Z, S]		剩余输入: b>0){b=b-1;if(c>0){c=0;}}}}		产生式: A->SZ
分析栈: [}, }, T, ), A, >, Z, X, H]		剩余输入: b>0){b=b-1;if(c>0){c=0;}}}}		产生式: S->HX
分析栈: [}, }, T, ), A, >, Z, X, i]		剩余输入: b>0){b=b-1;if(c>0){c=0;}}}}		产生式: H->i
分析栈: [}, }, T, ), A, >, Z, X]		剩余输入: >0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, T, ), A]		剩余输入: 0){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, T, ), Z, S]		剩余输入: 0){b=b-1;if(c>0){c=0;}}}}		产生式: A->SZ
分析栈: [}, }, T, ), Z, X, H]		剩余输入: 0){b=b-1;if(c>0){c=0;}}}}		产生式: S->HX
分析栈: [}, }, T, ), Z, X, n]		剩余输入: 0){b=b-1;if(c>0){c=0;}}}}		产生式: H->n
分析栈: [}, }, T, ), Z, X]		剩余输入: ){b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, T]		剩余输入: {b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, }, F, {]		剩余输入: {b=b-1;if(c>0){c=0;}}}}		产生式: T->{F}
分析栈: [}, }, }, F]		剩余输入: b=b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, }, G]		剩余输入: b=b-1;if(c>0){c=0;}}}}		产生式: F->G
分析栈: [}, }, }, V, ;, A, =, i]		剩余输入: b=b-1;if(c>0){c=0;}}}}		产生式: G->i=A;V
分析栈: [}, }, }, V, ;, A, =]		剩余输入: =b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, }, V, ;, A]		剩余输入: b-1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, }, V, ;, Z, S]		剩余输入: b-1;if(c>0){c=0;}}}}		产生式: A->SZ
分析栈: [}, }, }, V, ;, Z, X, H]		剩余输入: b-1;if(c>0){c=0;}}}}		产生式: S->HX
分析栈: [}, }, }, V, ;, Z, X, i]		剩余输入: b-1;if(c>0){c=0;}}}}		产生式: H->i
分析栈: [}, }, }, V, ;, Z, X]		剩余输入: -1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, }, V, ;, Z, S, -]		剩余输入: -1;if(c>0){c=0;}}}}		产生式: Z->-SZ
分析栈: [}, }, }, V, ;, Z, S]		剩余输入: 1;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, }, V, ;, Z, X, H]		剩余输入: 1;if(c>0){c=0;}}}}		产生式: S->HX
分析栈: [}, }, }, V, ;, Z, X, n]		剩余输入: 1;if(c>0){c=0;}}}}		产生式: H->n
分析栈: [}, }, }, V, ;, Z, X]		剩余输入: ;if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, }, V]		剩余输入: if(c>0){c=0;}}}}		产生式: 
分析栈: [}, }, }, F]		剩余输入: if(c>0){c=0;}}}}		产生式: V->F
分析栈: [}, }, }, B]		剩余输入: if(c>0){c=0;}}}}		产生式: F->B
分析栈: [}, }, }, T, ), D, (, f]		剩余输入: if(c>0){c=0;}}}}		产生式: B->f(D)T
分析栈: [}, }, }, T, ), D, (]		剩余输入: (c>0){c=0;}}}}		产生式: 
分析栈: [}, }, }, T, ), D]		剩余输入: c>0){c=0;}}}}		产生式: 
分析栈: [}, }, }, T, ), A, >, A]		剩余输入: c>0){c=0;}}}}		产生式: D->A>A
分析栈: [}, }, }, T, ), A, >, Z, S]		剩余输入: c>0){c=0;}}}}		产生式: A->SZ
分析栈: [}, }, }, T, ), A, >, Z, X, H]		剩余输入: c>0){c=0;}}}}		产生式: S->HX
分析栈: [}, }, }, T, ), A, >, Z, X, i]		剩余输入: c>0){c=0;}}}}		产生式: H->i
分析栈: [}, }, }, T, ), A, >, Z, X]		剩余输入: >0){c=0;}}}}		产生式: 
分析栈: [}, }, }, T, ), A]		剩余输入: 0){c=0;}}}}		产生式: 
分析栈: [}, }, }, T, ), Z, S]		剩余输入: 0){c=0;}}}}		产生式: A->SZ
分析栈: [}, }, }, T, ), Z, X, H]		剩余输入: 0){c=0;}}}}		产生式: S->HX
分析栈: [}, }, }, T, ), Z, X, n]		剩余输入: 0){c=0;}}}}		产生式: H->n
分析栈: [}, }, }, T, ), Z, X]		剩余输入: ){c=0;}}}}		产生式: 
分析栈: [}, }, }, T]		剩余输入: {c=0;}}}}		产生式: 
分析栈: [}, }, }, }, F, {]		剩余输入: {c=0;}}}}		产生式: T->{F}
分析栈: [}, }, }, }, F]		剩余输入: c=0;}}}}		产生式: 
分析栈: [}, }, }, }, G]		剩余输入: c=0;}}}}		产生式: F->G
分析栈: [}, }, }, }, V, ;, A, =, i]		剩余输入: c=0;}}}}		产生式: G->i=A;V
分析栈: [}, }, }, }, V, ;, A, =]		剩余输入: =0;}}}}		产生式: 
分析栈: [}, }, }, }, V, ;, A]		剩余输入: 0;}}}}		产生式: 
分析栈: [}, }, }, }, V, ;, Z, S]		剩余输入: 0;}}}}		产生式: A->SZ
分析栈: [}, }, }, }, V, ;, Z, X, H]		剩余输入: 0;}}}}		产生式: S->HX
分析栈: [}, }, }, }, V, ;, Z, X, n]		剩余输入: 0;}}}}		产生式: H->n
分析栈: [}, }, }, }, V, ;, Z, X]		剩余输入: ;}}}}		产生式: 
分析栈: [}, }, }, }, V]		剩余输入: }}}}		产生式: 
分析栈: [}, }, }]		剩余输入: }}}		产生式: 
分析栈: [}, }]		剩余输入: }}		产生式: 
分析栈: [}]		剩余输入: }		产生式: 
分析栈: []		剩余输入: 		产生式: 
语法分析通过!!
```