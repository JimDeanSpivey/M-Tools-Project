APIROU04 ;AU - API TEST ; JUN 18,2012
 ;;3.0;TESTING;;JUN 18,2012
 ;
 Q
 ;
INDOBLK ;
 D CALL1^APIROU03
 F I=1:5:1 D
 . S Y=$GET(Y)+1
 Q
 ;
ASSUMEV1 ;
 S V1=V1+1
 Q
 ;
ASSUMEV2 
 N A
 F I=1:5:1 D
 . D ASSUMEV1
 . N T
LOOP . S T=T+1
 . I T>4 Q
 . W M
 . G LOOP
 ;
ASSUMEV3
 S V3=V3+1
 Q
