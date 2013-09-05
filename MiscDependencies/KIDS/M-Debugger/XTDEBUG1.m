XTDEBUG1 ;JLI/OAK_OIFO- ;06/10/13
 ;;7.3;TOOLKIT;**107**;Apr 25, 1995;Build 14
 ;;Per VHA Directive 2004-038, this routine should not be modified
 D EN^XTMUNIT("ZZUTXTD1")
 Q
 ;
STRTCMND ;
 N XTDEBFOR,XTDEBLOC,XTDEBPC1,XTDEBLVL,XTDEBARG,XTDEBCOD,XTDEBLIN
 N XTDEBTR1 ;XTDEBTRU
 S XTDEBLOC=$$GETGLOB()
 S XTDEBLVL=@XTDEBLOC@("LASTLVL")
 S XTDEBLIN=@XTDEBLOC@("LVL",XTDEBLVL,"CMND")
 M XTDEBARG=@XTDEBLOC@("LVL",XTDEBLVL,"XTDEBARG")
 D INFO^XTMLOG("ENTERED STARTCMND","XTDEBLIN")
 D DEBUG^XTMLOG("STRTCMND","XTDEBLIN,XTDEBARG",1)
 S @XTDEBLOC@("LVL",XTDEBLVL,"ENTRY")="COMMANDS^XTDEBUG"
 S @XTDEBLOC@("LVL",XTDEBLVL,"ARGS","CURR")=""
 D INFO^XTMLOG("FALLTHROUGH TO COMMANDS")
 G COMMANDS
 ;ABCDE GHIJKLMNOP RSTUVWXYZ   F Q
COMMANDS ;
 N XTDEBLOC,XTDEBCNT,XTDEBCON,XTDEBFUN,XTDEBLIN,XTDEBLVL,XTDEBQUW
 N XTDEBNUM,XTDEBRES,XTDEBUGX,XTDEBXXX,XTDEBARG,XTDEBQUI,XTDEBDAT
 N XTDEBINP,XTDEBL2A,XTDEBTR1,XTDEBROU
 ; ZEXCEPT: XTDEBRTN  - is used to hold return values from $$ or Q code
 ; ZEXCEPT: XTDEBTRU  - is used to hold equivalent of $T (true or false on last conditional)
 D INFO^XTMLOG("ENTER COMMANDS")
 S XTDEBLOC=$$GETGLOB()
 S XTDEBLVL=$G(@XTDEBLOC@("LASTLVL")) D:XTDEBLVL="" INFO^XTMLOG("EXIT COMMANDS 1A") S:XTDEBLVL="" @XTDEBLOC@("NXTCMD")="Q" Q:XTDEBLVL=""  D:XTDEBLVL<0 INFO^XTMLOG("EXIT COMMANDS 1B") I XTDEBLVL<0 S @XTDEBLOC@("NXTCMD")="Q" Q
 S XTDEBLIN=$G(@XTDEBLOC@("LVL",XTDEBLVL,"CMND"))
 S XTDEBROU=@XTDEBLOC@("LVL",XTDEBLVL,"ROUTINE")
 ;
 D DEBUG^XTMLOG("IN COMMANDS","XTDEBLIN,XTDEBLVL,"_$NA(@XTDEBLOC@("REASONDONE")))
 D DEBUG^XTMLOG("COMMANDS","XTDEBLIN,XTDEBLVL,"_$NA(@XTDEBLOC@("REASONDONE"))_","_$NA(@XTDEBLOC@("LVL",XTDEBLVL,"ARGS","CURR")))
 I $G(@XTDEBLOC@("REASONDONE"))="" D  D DEBUG^XTMLOG("COMMANDS1B","XTDEBDAT") I XTDEBDAT S @XTDEBLOC@("LVL",XTDEBLVL,"ENTRY")="COMMANDS^XTDEBUG" D INFO^XTMLOG("EXIT COMMANDS 2") S @XTDEBLOC@("NXTCMD")="Q" Q
 . N XTDEBNUM,XTDEBVAL S XTDEBDAT=0
 . S XTDEBNUM=$G(@XTDEBLOC@("LVL",XTDEBLVL,"ARGS","CURR"))+1
 . S XTDEBVAL=$D(@XTDEBLOC@("LVL",XTDEBLVL,"XTDEBARG","ARGS",XTDEBNUM))
 . D DEBUG^XTMLOG("COMMANDS1A","XTDEBNUM,XTDEBVAL")
 . I 'XTDEBVAL,XTDEBNUM>1 Q
 . S XTDEBDAT=$$CHKQUIT^XTDEBUG() ; JLI 051026  ALSO MOVED TO CHEKDONE
 . Q
 K @XTDEBLOC@("LVL",XTDEBLVL,"VALUESDONE")
 K @XTDEBLOC@("REASONDONE")
 ;W !,"COMMANDSA- XTDEBLVL=",XTDEBLVL,"  XTDEBNUM=",$G(@XTDEBLOC@("LVL",XTDEBLVL,"ARGS","CURR"))
 S XTDEBNUM=$G(@XTDEBLOC@("LVL",XTDEBLVL,"ARGS","CURR"))+1
 S @XTDEBLOC@("LVL",XTDEBLVL,"ARGS","CURR")=XTDEBNUM
 ;W !,"COMMANDS- XTDEBLVL=",XTDEBLVL,"  XTDEBNUM=",XTDEBNUM
 M XTDEBARG=@XTDEBLOC@("LVL",XTDEBLVL,"XTDEBARG")
 D DEBUG^XTMLOG("IN COMMANDS",$NA(@XTDEBLOC@("LVL")))
 ;W ! ZW XTDEBARG W !
 ;  070225  Pre-processing for $$ and $S
 S XTDEBINP=$G(XTDEBARG("ARGS",XTDEBNUM)),XTDEBL2A=""
 I XTDEBINP'="",XTDEBINP'["XTDEBV(",((XTDEBINP["$$")!(XTDEBINP["$S")) S XTDEBL2A=$$CHKARGS^XTDEBUG(.XTDEBINP) I XTDEBL2A'="" D  D INFO^XTMLOG("COMMANDS GO TO PREPROCS") G PREPROCS^XTDEBUG
 . S XTDEBARG("ARGS",XTDEBNUM,"ORIGINAL")=XTDEBARG("ARGS",XTDEBNUM)
 . S XTDEBARG("ARGS",XTDEBNUM)=XTDEBINP
 . M @XTDEBLOC@("LVL",XTDEBLVL,"XTDEBARG")=XTDEBARG
 . S @XTDEBLOC@("LVL",XTDEBLVL,"PRE-PROCESS1")=XTDEBINP
 . S @XTDEBLOC@("LVL",XTDEBLVL,"PRE-PROCESS")=XTDEBL2A
 . Q
 ; 070225 - end of pre-processing insert
 S XTDEBINP=$G(XTDEBARG("ARGS",XTDEBNUM))
 ; 080420 - $T WITHOUT A ROUTINE, NEEDS TO HAVE THE ROUTINE ADDED
 I XTDEBINP'="",((XTDEBINP["$T(")!(XTDEBINP["$TEXT(")) S XTDEBINP=$$DOLRTEXT^XTDEBUG(XTDEBINP,@XTDEBLOC@("LVL",XTDEBLVL,"ROUTINE")),XTDEBARG("ARGS",XTDEBNUM)=XTDEBINP
 ;
 D DEBUG^XTMLOG("COMMANDS1","XTDEBLIN")
 D DEBUG^XTMLOG("COMMANDS1","XTDEBARG",1)
 D DEBUG^XTMLOG("IN COMMANDS LEVEL=","XTDEBLVL")
 D DEBUG^XTMLOG("LINE=","XTDEBLIN")
 I XTDEBNUM>1,('$D(XTDEBARG("ARGS",XTDEBNUM))) D DEBUG^XTMLOG("COMMANDS1EXIT","XTDEBNUM") G EXITCMND
 D INFO^XTMLOG("FALLTHROUGH TO PRECOND")
 ;
PRECOND ;
 D INFO^XTMLOG("ENTER PRECOND")
 S XTDEBCON=1 I $D(XTDEBARG("PRECOND")) S XTDEBUGX="S XTDEBCON="_XTDEBARG("PRECOND") D EXECUTE(XTDEBUGX,XTDEBROU) I 'XTDEBCON K XTDEBARG
 ;
 D DEBUG^XTMLOG("COMMANDS1Z","XTDEBCON,"_$NA(@XTDEBLOC@("LVL")),1)
 ;
 I $G(XTDEBARG("CMND"))="" S XTDEBARG("CMND")="Q"
 I XTDEBCON,XTDEBARG("CMND")="F" D INFO^XTMLOG("PRECOND GO TO STRTFOR") G STRTFOR^XTDEBUG
 D DEBUG^XTMLOG("COMMANDSZ3","XTDEBARG(""CMND"")")
 D INFO^XTMLOG("FALLTHROUGH TO QUIT")
 ;
QUIT ;
 I XTDEBCON,XTDEBARG("CMND")?1(1"Q",1"q") D  G:$G(@XTDEBLOC@("LVL",XTDEBLVL-1,"IN FOR")) NEXTFOR^XTDEBUG G LEAVETAG^XTDEBUG
 . I $G(@XTDEBLOC@("LVL",XTDEBLVL-1,"IN FOR")) S ^("FOR QUIT")=1
 . S XTDEBQUI=1 K XTDEBRTN
 . ; TODO: IN NEXT LINE NEED TO IDENTIFY THINGS LIKE $$HTFM($H) AND ADD ROUTINE TO IT.
 . ;W !,"COMMANDS+48 VALUE=",$G(VALUE),"   VALUE1=",$G(VALUE1)
 . D INFO^XTMLOG("IN QUIT","XTDEBARG(""ARGS"",1)")
 . I $G(XTDEBARG("ARGS",1))'="" S XTDEBXXX="S XTDEBRTN="_XTDEBARG("ARGS",1) D EXECUTE(XTDEBXXX,XTDEBROU)
 . S @XTDEBLOC@("LVL",XTDEBLVL,"CMND")=""
 . K @XTDEBLOC@("LVL",XTDEBLVL,"ENTRY") ; FORCE A NEW LINE
 . D DEBUG^XTMLOG("SAW A QUIT")
 . Q
 ;
DO ;
 I XTDEBCON,XTDEBARG("CMND")?1(1"D",1"d") D  I XTDEBCON G OPENDO^XTDEBUG ;(XTDEBARG("ARGS",XTDEBNUM))
 . I $D(XTDEBARG("ARGS",XTDEBNUM,"POSTCOND")) S XTDEBUGX="S XTDEBCON="_XTDEBARG("ARGS",XTDEBNUM,"POSTCOND") D EXECUTE(XTDEBUGX,XTDEBROU) I 'XTDEBCON Q
 . N ARG S ARG=$G(XTDEBARG("ARGS",XTDEBNUM))
 . I ARG["^DIQ"!(ARG["^DIC")!(ARG["^DIE")!(ARG["^DIK")!(ARG["^%DT") D EXECUTE("D "_XTDEBARG("ARGS",XTDEBNUM)) S XTDEBCON=0
 . ; handle argumentless DO as DO to next line, but indicate number of periods to move over
 . I $G(XTDEBARG("ARGS",XTDEBNUM))="" D
 . . S @XTDEBLOC@("LVL",XTDEBLVL+1,"PERIODS")=$G(@XTDEBLOC@("LVL",XTDEBLVL,"PERIODS"))+1
 . . I $G(@XTDEBLOC@("LVL",XTDEBLVL-1,"XTDEBARG","CMND"))="F" D
 . . . S @XTDEBLOC@("LVL",XTDEBLVL+1,"PERIODS")=$G(@XTDEBLOC@("LVL",XTDEBLVL-2,"PERIODS"))+1
 . . N XTDEBHER
 . . S XTDEBHER="+"_(@XTDEBLOC@("LVL",XTDEBLVL,"LINE")+1)_"^"_@XTDEBLOC@("LVL",XTDEBLVL,"ROUTINE")
 . . S XTDEBARG("ARGS",XTDEBNUM)=XTDEBHER
 . . Q
 . S @XTDEBLOC@("LVL",XTDEBLVL,"OPENDO")=XTDEBARG("ARGS",XTDEBNUM),^("ENTRY")="DOLINE1^XTDEBUG"
 . D DEBUG^XTMLOG("SET FOR OPENDO",$NA(@XTDEBLOC@("LVL")),1)
 . Q
 ;
GOTO ;
 I XTDEBCON,XTDEBARG("CMND")?1(1"G",1"g") D  I XTDEBCON G GOTOCMD
 . I $D(XTDEBARG("ARGS",XTDEBNUM,"POSTCOND")) S XTDEBUGX="S XTDEBCON="_XTDEBARG("ARGS",XTDEBNUM,"POSTCOND") D EXECUTE(XTDEBUGX,XTDEBROU) I 'XTDEBCON Q
 . Q
 ;
IF ;
 I XTDEBCON,$E(XTDEBARG("CMND"))?1(1"I",1"i") D
 . S XTDEBTRU=0,XTDEBTR1=XTDEBARG("ARGS",XTDEBNUM) S XTDEBXXX="I "_XTDEBTR1_" S XTDEBTRU=1" D EXECUTE(XTDEBXXX,XTDEBROU) S @XTDEBLOC@("LVL",XTDEBLVL,"CMND")=$S(XTDEBTRU:XTDEBLIN,1:"")
 . D DEBUG^XTMLOG("IF...","XTDEBTRU,XTDEBXXX,"_$NA(@XTDEBLOC@("LVL",XTDEBLVL,"CMND")))
 . Q
 ;
ELSE ;
 I XTDEBCON,$E(XTDEBARG("CMND"))?1(1"E",1"e") D
 . D DEBUG^XTMLOG("ELSE...",$NA(@XTDEBLOC@("LVL",XTDEBLVL,"TRUE")))
 . I XTDEBTRU S @XTDEBLOC@("LVL",XTDEBLVL,"CMND")=""
 . Q
 ;
KILL ;
 ; check for exclusive or global kill - will kill session, so terminate
 I XTDEBCON,XTDEBARG("CMND")?1(1"K",1"k"),(XTDEBARG("ARGS",XTDEBNUM)="")!($E(XTDEBARG("ARGS",XTDEBNUM))="(") S @XTDEBLOC@("NXTCMD")="Q" Q  ; D REASON^XTDEBUG("EXCLKILL") Q
 I XTDEBCON,XTDEBARG("CMND")?1(1"K",1"k") S XTDEBXXX="K "_XTDEBARG("ARGS",XTDEBNUM) D EXECUTE(XTDEBXXX,XTDEBROU) D DEBUG^XTMLOG("COMMANDS_KILL","XTDEBXXX")
 ; TODO: if args contain intrinsic functions, resolve it to the value
 I XTDEBCON,$D(XTDEBARG("ARGS",XTDEBNUM)),XTDEBARG("ARGS",XTDEBNUM)["$$" D
 . ; TODO: if part of $SELECT, then resolve it to the value to be returned
 . Q
 ;
NEW ;
 I XTDEBCON,XTDEBARG("CMND")?1(1"N",1"n") D NEWVARS^XTDEBUG(.XTDEBARG)
 ;
WRITE ;
 I XTDEBCON,XTDEBARG("CMND")?1(1"W",1"w") G WRITECMD
 ; S  (CHJKLMO
SETDOLR ;
 I XTDEBCON,$E(XTDEBARG("CMND"))?1(1"S",1"s"),XTDEBARG("ARGS",XTDEBNUM)["=$$" D  I XTDEBCON G OPENDO^XTDEBUG
 . I $D(XTDEBARG("ARGS",XTDEBNUM,"POSTCOND")) S XTDEBUGX="S XTDEBCON="_XTDEBARG("ARGS",XTDEBNUM,"POSTCOND") D EXECUTE(XTDEBUGX,XTDEBROU) I 'XTDEBCON Q
 . N ARG S ARG=$G(XTDEBARG("ARGS",XTDEBNUM))
 . I ARG["^DIQ"!(ARG["^DIC")!(ARG["^DIE")!(ARG["^DIK")!(ARG["^%DT") D EXECUTE("S "_XTDEBARG("ARGS",XTDEBNUM)) S XTDEBCON=0
 . S @XTDEBLOC@("LVL",XTDEBLVL,"OPENDO")=$P(XTDEBARG("ARGS",XTDEBNUM),"=$$",2)
 . S @XTDEBLOC@("LVL",XTDEBLVL,"XTDEBVAR")=$P(XTDEBARG("ARGS",XTDEBNUM),"=$$")
 . D DEBUG^XTMLOG("GOING TO OPENDO",$NA(@XTDEBLOC@("LVL")),1)
 . Q
 ;
SET ;
 I XTDEBCON,$E(XTDEBARG("CMND"))?1(1"S",1"s"),XTDEBARG("ARGS",XTDEBNUM)'["=$$" D
 . I $D(XTDEBARG("ARGS",XTDEBNUM,"POSTCOND")) S XTDEBUGX="S XTDEBCON="_XTDEBARG("ARGS",XTDEBNUM,"POSTCOND") D EXECUTE(XTDEBUGX,XTDEBROU) I 'XTDEBCON Q
 . S XTDEBUGX=XTDEBARG("CMND")_" "_XTDEBARG("ARGS",XTDEBNUM)
 . D DEBUG^XTMLOG("COMMANDS1SET","XTDEBUGX,"_$NA(@XTDEBLOC@("LVL")),1)
 . D DEBUG^XTMLOG("EXECUTE FOR SET","XTDEBUGX,XTDEBARG",1)
 . D EXECUTE(XTDEBUGX,XTDEBROU) D DEBUG^XTMLOG("EXECUTE FOR SET DONE")
 . Q
 ;
MERGE ;
 I XTDEBCON,$E(XTDEBARG("CMND"))?1(1"M",1"m"),XTDEBARG("ARGS",XTDEBNUM)'["=$$" D
 . I $D(XTDEBARG("ARGS",XTDEBNUM,"POSTCOND")) S XTDEBUGX="S XTDEBCON="_XTDEBARG("ARGS",XTDEBNUM,"POSTCOND") D EXECUTE(XTDEBUGX,XTDEBROU) I 'XTDEBCON Q
 . S XTDEBUGX=XTDEBARG("CMND")_" "_XTDEBARG("ARGS",XTDEBNUM)
 . D DEBUG^XTMLOG("COMMANDS1MERGE","XTDEBUGX,"_$NA(@XTDEBLOC@("LVL")),1)
 . D DEBUG^XTMLOG("EXECUTE FOR SET","XTDEBUGX,XTDEBARG",1)
 . D EXECUTE(XTDEBUGX,XTDEBROU)
 . Q
 ;
COMMENT ;
 I XTDEBCON,XTDEBARG("CMND")=";" D  ; comment
 . S @XTDEBLOC@("LVL",XTDEBLVL,"CMND")=""
 . K @XTDEBLOC@("LVL",XTDEBLVL,"ENTRY") ; FORCE A NEW LINE
 . Q
 ;
READ ;
 I XTDEBCON,XTDEBARG("CMND")?1(1"R",1"r") G READCMD^XTDEBUG2 ; READ command
 ;
 ;I XTDEBARG("CMND")'="" X XTDEBCOD
 ;S XTDEBLNM=XTDEBLNM+1,@XTDEBLOC@("CURRLINE")=XTDEBLNM
 ;
FINISH ;
 I '$D(XTDEBARG("CMND")) M XTDEBARG=@XTDEBLOC@("LVL",XTDEBLVL,"XTDEBARG")
 I 'XTDEBCON,XTDEBARG("CMND")="F" G EXITCMND ; FOR GOES TO END OF LINE
 I $G(XTDEBQUI) G EXITCMND ; QUIT OFF OF LINE, AT LEAST
 S @XTDEBLOC@("LVL",XTDEBLVL,"ARGS","CURR")=XTDEBNUM D DEBUG^XTMLOG("COMMANDS2","XTDEBNUM")
 D DEBUG^XTMLOG("COMMANDS2","XTDEBNUM")
 G COMMANDS
 ;
EXITCMND ;
 N XTDEBLOC,XTDEBLVL
 S XTDEBLOC=$$GETGLOB(),XTDEBLVL=@XTDEBLOC@("LASTLVL")
 D DEBUG^XTMLOG("EXITCMND","XTDEBLVL,"_$NA(@XTDEBLOC@("LVL")),1)
 I $G(@XTDEBLOC@("LVL",XTDEBLVL,"CMND"))="",'$D(@XTDEBLOC@("LVL",XTDEBLVL-1,"IN FOR")) K @XTDEBLOC@("LVL",XTDEBLVL,"ENTRY") D INFO^XTMLOG("KILLED ENTRY")
 E  S @XTDEBLOC@("LVL",XTDEBLVL,"ENTRY")="DOLINE1^XTDEBUG" D INFO^XTMLOG("SET ENTRY TO DOLINE1")
 D INFO^XTMLOG("EXITCMND TO NEXTENT")
 S @XTDEBLOC@("NXTCMD")="" D VALUES^XTDEBUG3 Q
 ;
DOLINE ; (XTDEBLIN,XTDEBTYP) ;
 N XTDEBLOC,XTDEBLVL,XTDEBLIN
 S XTDEBLOC=$$GETGLOB()
 D DEBUG^XTMLOG("Entered DOLINE")
 S XTDEBLVL=$G(@XTDEBLOC@("LASTLVL")) ;+1 I XTDEBLVL'>0 S XTDEBLVL=XTDEBLVL+1,
 S XTDEBLIN=$G(@XTDEBLOC@("LVL",XTDEBLVL,"CMND"))
 I XTDEBLIN="",$G(@XTDEBLOC@("LVL",XTDEBLVL-1,"IN FOR")) G NEXTFOR^XTDEBUG
 I XTDEBLIN="" S @XTDEBLOC@("NXTCMD")="" D VALUES^XTDEBUG3 Q
 ;S ^TMP("XTDEBUG1",$J,XTDEBLVL,"XTDEBLIN")=XTDEBLIN
 ;S @XTDEBLOC@("LASTLVL")=XTDEBLVL
 S @XTDEBLOC@("LVL",XTDEBLVL,"ENTRY")="DOLINE1^XTDEBUG"
 ;S @XTDEBLOC@("LVL",XTDEBLVL,"LINE")=XTDEBLIN
 D DEBUG^XTMLOG("Leaving DOLINE")
 G DOLINE1
 ;
DOLINE1 ;
 N XTDEBLOC,XTDEBLVL,XTDEBLIN,XTDEBARG,XTDEBPER
 D DEBUG^XTMLOG("Entering DOLINE1")
 S XTDEBLOC=$$GETGLOB()
 S XTDEBLVL=@XTDEBLOC@("LASTLVL")
 S XTDEBLIN=$G(@XTDEBLOC@("LVL",XTDEBLVL,"CMND"))
 ;
 I '$D(@XTDEBLOC@("LVL",XTDEBLVL,"CMND PERIODS")) S @XTDEBLOC@("LVL",XTDEBLVL,"CMND PERIODS")=$$GETPERIO^XTDEBUG(.XTDEBLIN)
 S XTDEBPER=@XTDEBLOC@("LVL",XTDEBLVL,"CMND PERIODS")
 D DEBUG^XTMLOG("XTDEBPER","XTDEBPER,"_$NA(@XTDEBLOC@("LVL",XTDEBLVL,"PERIODS")))
 I $G(@XTDEBLOC@("LVL",XTDEBLVL,"PERIODS"))>0,$G(@XTDEBLOC@("LVL",XTDEBLVL,"PERIODS"))>XTDEBPER D  G POPLEVEL^XTDEBUG
 . D DEBUG^XTMLOG("TOO FEW PERIODS, EXIT ARGLESS DO")
 . Q
 I XTDEBPER>0,XTDEBPER>$G(@XTDEBLOC@("LVL",XTDEBLVL,"PERIODS")) D  S @XTDEBLOC@("NXTCMD")="" D VALUES^XTDEBUG3 Q  ; SKIP OVER
 . S @XTDEBLOC@("LVL",XTDEBLVL,"CMND")=""
 . K @XTDEBLOC@("LVL",XTDEBLVL,"ENTRY")
 . D DEBUG^XTMLOG("SKIPPING EXCESS PERIODS")
 . Q
 ;
 D DEBUG^XTMLOG("DOLINE1","XTDEBLIN,XTDEBLVL")
 I XTDEBLIN'="" D  G STRTCMND ; S @XTDEBLOC@("LASTLVL")=XTDEBLVL-1 K @XTDEBLOC@("LVL",XTDEBLVL) G NEXTENT^XTDEBUG ; GO GET NEXT LINE
 . S @XTDEBLOC@("LVL",XTDEBLVL,"CMND")=$$GETCMND^XTDEBUG(.XTDEBARG,XTDEBLIN)
 . K @XTDEBLOC@("LVL",XTDEBLVL,"XTDEBARG")
 . M @XTDEBLOC@("LVL",XTDEBLVL,"XTDEBARG")=XTDEBARG
 . D DEBUG^XTMLOG("DOLINE1 TO STRTCMND")
 . Q
 I $G(@XTDEBLOC@("LVL",XTDEBLVL-1,"IN FOR")) G NEXTFOR^XTDEBUG
 S @XTDEBLOC@("LVL",XTDEBLVL,"ENTRY")=""
 D DEBUG^XTMLOG("DOLINE1 TO NEXTENT")
 S @XTDEBLOC@("NXTCMD")="" D VALUES^XTDEBUG3 Q
 ;
GOTOCMD ;
 ; ZEXCEPT: XTDEBLOC,XTDEBLVL,XTDEBARG,XTDEBNUM  - NEWed and defined in COMMANDS
 N XTDEBROU,XTDEBLIN,XTDEBCMD,XTDEBMRK,XTDEBCNT,XTDEBI
 D DEBUG^XTMLOG("IN GOTOCMD")
 S XTDEBROU=$P(XTDEBARG("ARGS",XTDEBNUM),U,2) I XTDEBROU="" S XTDEBROU=@XTDEBLOC@("LVL",XTDEBLVL,"ROUTINE")
 S XTDEBLIN=$P(XTDEBARG("ARGS",XTDEBNUM),U) I XTDEBLIN="" S XTDEBLIN="+1"
 S XTDEBCMD=$$GETLINE^XTDEBUG(XTDEBROU,XTDEBLIN)
 S XTDEBMRK=$$STKFROM^XTDEBUG(XTDEBLOC,XTDEBLVL)
 S XTDEBCNT=0 F XTDEBI=0:0 S XTDEBI=$O(@XTDEBLOC@("STK",XTDEBLVL,XTDEBI)) Q:XTDEBI'>0  S XTDEBCNT=XTDEBI
 S XTDEBCNT=XTDEBCNT+1
 S @XTDEBLOC@("STK",XTDEBLVL,XTDEBCNT)="G:  "_XTDEBLIN_U_XTDEBROU_XTDEBMRK
 S @XTDEBLOC@("LVL",XTDEBLVL,"ROUTINE")=XTDEBROU
 S @XTDEBLOC@("LVL",XTDEBLVL,"LINE")=$$LINENUM^XTDEBUG(XTDEBROU,XTDEBLIN)-1
 S @XTDEBLOC@("LVL",XTDEBLVL,"CODE")=""
 S @XTDEBLOC@("LVL",XTDEBLVL,"CMND")=""
 S XTDEBCMD=$P(XTDEBCMD," ",2,999)
 S XTDEBCNT=$$GETPERIO^XTDEBUG(.XTDEBCMD)
 S @XTDEBLOC@("LVL",XTDEBLVL,"PERIODS")=XTDEBCNT
 D DEBUG^XTMLOG("IN GOTOCMD",$NA(@XTDEBLOC@("LVL")),1)
 S @XTDEBLOC@("NXTCMD")="" D VALUES^XTDEBUG3 Q
 ;
WRITECMD ;
 ; ZEXCEPT: XTDEBARG,XTDEBNUM,XTDEBQUW - VARIABLES NEWED IN COMMANDS
 ; ZEXCEPT: XTMUNIT - IF DEFINED, IS NEWED AND DEFINED IN EN^XTMUNIT
 D WRITCMND
 I $G(XTDEBQUW)=1 K XTDEBQUW I '$D(XTMUNIT) D  Q
 . N XXX S XXX=XTDEBARG("ARGS",XTDEBNUM)
 . F  Q:$E(XXX,1)'="!"  S XXX=$E(XXX,2,$L(XXX)) D REASON^XTDEBUG("WRITE",XTDEBNUM)
 . Q
 G FINISH
 ;
WRITCMND ;
 N XTDEBXXY,XTDEBRES,XTDEBXXX
 ; ZEXCEPT: XTDEBARG,XTDEBLOC,XTDEBNUM,XTDEBQUW - VARIABLES NEWED IN COMMANDS
 D DEBUG^XTMLOG("ENTERED WRITECMD","XTDEBARG") ; 070820
 I (IO'=IO(0))!('$$BROKER^XWBLIB()) D
 . I XTDEBARG("ARGS",XTDEBNUM)["!" D  K @XTDEBLOC@("CONSOLE-OUT") I 1
 . . N XXX S XXX=XTDEBARG("ARGS",XTDEBNUM)
 . . F  Q:$E(XXX,1)'="!"  S XXX=$E(XXX,2,$L(XXX)) W !
 . . Q
 . E  D
 . . N VALUE S VALUE=XTDEBARG("ARGS",XTDEBNUM)
 . . I $E(VALUE,1)="?" S VALUE=$$QUERYNUM^XTDEBUG(VALUE)
 . . S @("XTDEBXXX="_VALUE) W XTDEBXXX
 . . S @XTDEBLOC@("CONSOLE-OUT")=$G(@XTDEBLOC@("CONSOLE-OUT"))_$S($E(VALUE)="""":$E(VALUE,2,$L(VALUE)-1),1:@VALUE)
 . . Q
 . Q
 I $$BROKER^XWBLIB()&(IO=IO(0)) D
 . S ^("CONSOLE-OUT")=$G(@XTDEBLOC@("CONSOLE-OUT"))
 . S XTDEBRES=$$RESULTS^XTDEBUG()
 . N XTDEBVAL S XTDEBVAL=XTDEBARG("ARGS",XTDEBNUM)
 . I XTDEBARG("ARGS",XTDEBNUM)["!" S XTDEBQUW=1 Q  ;S XTDEBCNT=$G(@XTDEBRES@("CONSOLE-OUT"))+1 Q
 . I $E(XTDEBARG("ARGS",XTDEBNUM),1)="?" S XTDEBVAL=$$QUERYNUM^XTDEBUG(XTDEBARG("ARGS",XTDEBNUM)),XTDEBARG("ARGS",XTDEBNUM)=XTDEBVAL
 . I $E(XTDEBVAL)=$C(34),$E(XTDEBVAL,$L(XTDEBVAL))=$C(34) S XTDEBVAL=$E(XTDEBVAL,2,$L(XTDEBVAL)-1)
 . ; E  I XTDEBVAL'="" S XTDEBXXY="S XTDEBVAL=$G("_XTDEBARG("ARGS",XTDEBNUM)_")" X XTDEBXXY
 . I XTDEBVAL'="" S XTDEBXXY="S XTDEBVAL="_XTDEBARG("ARGS",XTDEBNUM) D EXECUTE(XTDEBXXY,XTDEBROU)
 . S ^("CONSOLE-OUT")=@XTDEBLOC@("CONSOLE-OUT")_XTDEBVAL
 . D DEBUG^XTMLOG("NEW CONSOLE-OUT",$NA(@XTDEBLOC@("CONSOLE-OUT")))
 . Q
 Q
 ;
GETGLOB() ;
 Q $$GETGLOB^XTDEBUG()
 ;
EXECUTE(XTDEBUGX,XTDEBROU) ;
 N XTDEBUGI,XTDEBUGEX,XTDEBUGRT,XTDEBUGX1,XTDEBUGP,XTDEBUGCH
 F XTDEBUGI=2:1 S XTDEBUGP=$P(XTDEBUGX,"$$",XTDEBUGI) Q:XTDEBUGP=""  D
 . S XTDEBUGX1=$P(XTDEBUGX,"$$",1,XTDEBUGI-1)
 . S XTDEBUGEX=$P(XTDEBUGX,"$$",XTDEBUGI,999),XTDEBUGRT=""
 . F  S XTDEBUGCH=$E(XTDEBUGEX,1,1) Q:XTDEBUGCH?.P&(XTDEBUGCH'="^")  D
 . . S XTDEBUGRT=XTDEBUGRT_XTDEBUGCH,XTDEBUGEX=$E(XTDEBUGEX,2,999)
 . S XTDEBUGX1=XTDEBUGX1_"$$"_XTDEBUGRT_$S($P(XTDEBUGRT,"^",2)="":"^"_XTDEBROU,1:"")_XTDEBUGEX
 . S XTDEBUGX=XTDEBUGX1
 D RESETLGR^XTDEBUG X XTDEBUGX D SETDOLT^XTDEBUG,SETLGR^XTDEBUG
 Q
