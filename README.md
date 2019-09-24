台灣臉霸 | Face8 API
======================

Platform
-------------------------------------
Android Studio 3.5


How to use?
--------------------------------------------------
* 前往[**官網**](https://www.face8.ai/api-doc/)申请api key。

1. 修改 \src\main\java\com\papagoinc\face8api\MainActivity.java 下面的對應的值:

> public static String api_key = "xxxx"; 	


2. 新增FaceSet (FACE8_API_CREATE)拿到faceset_token
	
> public static String api_faceset_token = "xxxx";    


3. 執行人臉偵測 (FACE8_API_FACEDETECT)拿到face_token

> public static String face_token = "xxxx";


4. 新增人臉進先前的faceset (FACE8_API_ADDPHOTO)


Notice
--------------------------------------------------
* test data - "\src\main\res\drawable\"

* image file - 目前僅支援 JPG, PNG


Fuction
------------------------------------------------------------
[FaceSet API] - 與操作 FaceSet 相關的功能，包括新增 FaceSet、將人臉加入 FaceSet 等等。

*FACE8_API_CREATE* : *創建 FaceSet*

*FACE8_API_ADDPHOTO* : *將人臉加入 FaceSet*

*FACE8_API_REMOVEFACE* : *移除 FaceSet 裡的人臉*

*FACE8_API_DELETE* : *刪除 FaceSet*

*FACE8_API_GETDETAIL* : *獲得 FaceSet 的資訊*


[Facial API] - 主要提供 人臉偵測、人臉比對以及人臉搜尋等功能。

*FACE8_API_FACEDETECT* : *人臉比對*

*FACE8_API_FACECOMPARE* : *人臉搜尋*

*FACE8_API_FACESEARCH* : *人臉偵測*


SDK Version
------------------------------------------------------------

  compileSdkVersion 28

  minSdkVersion 19

  targetSdkVersion 28


Library dependencies:
------------------------------------

 'com.squareup.okhttp3:okhttp:3.5.0' 

 'com.squareup.okhttp3:logging-interceptor:3.5.0' 
 


