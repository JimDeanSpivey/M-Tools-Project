RFIOD000 ; AU - ROUTINE FANIN/FANOUT TESTS ; JUN 30, 2013
 ;;3.0;TESTING;; JUN 12,2012
 ;
 Q
 ;
TOPD(A) ;
 I $D(A) D DD0^RFIOD001(A)
 D INTD(A)
 D COMD(A)
 D COMD^RFIOC000(A)
 D T0^RFIOXLLC
 Q
 ;
TOPE(A)
 N SUM,I S SUM=1
 D COMD^RFIOA001(A)
 F I=1:3:1 D
 . Q:$$DE0^RFIOA000(^G(A,I))="N"
 . S SUM=SUM+$$INTE(I)
 . S SUM=SUM+$$COME^RFIOC001(I)
 Q
 ;
FROM2 ;
 Q 
 ;
CE0(A) ;
 Q 1
 ;
INTD(A) ;
 Q
 ;
INTE(A) ;
 Q A
 ;
COMD(A) ;
 Q
 ;
COME(A) ;
 Q 1
 ;