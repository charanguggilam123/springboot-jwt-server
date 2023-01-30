# springboot-jwt-server

This project is a poc to demonstrate how to create a jwt server and use it to generate token and decode token and also verify signature of any provided token

Detailed Description: 

This application act as server to perform operation using JWT. Uses h2 repository to maintain users(for demo purpose)

Flow is:
we have created security config and implemented config to register users who can generate tokens and consume authenticated endpoints ("/test")
Created a custome filter implementing interface OncePerRequest which to executre before UsernamePasswordAuthenticationFilter in which we perform logic to verify user details based on token received and provide access. 
JWT token has 3 parts
	1. headers: which has algorithm used to encrypt
	2. payload: which containers subject details like user
	3. signature: used to verify authenticity of token provided
	In the filter once we receive token,we extract username from token and check if uer is present in database and then verify the expiry time and if its valid then
	we set in the execution context so the same will be used in next filters
	
For generating signature you can use 256 bit secret or a certificate. This project impl using secret but in tutorials provided in ref section there are impl with keys along with steps to generate cert and using it. will implement in future using cert and move this approach to another branch

There are some good tutorials in youtube explained with code. Please find few in the reference section

Refs:
 https://www.youtube.com/watch?v=KxqlJblhzfI
 https://www.youtube.com/watch?v=lA18U8dGKF8
 https://www.youtube.com/watch?v=KYNR5js2cXE