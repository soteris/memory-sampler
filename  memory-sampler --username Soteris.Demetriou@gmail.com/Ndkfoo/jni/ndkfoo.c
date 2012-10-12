#include <string.h>
#include <jni.h>

readPidMem(){
	File *fd;
	char[14] fileName = "/proc/meminfo";
	int fileSize;
	int position = 0;

	fd = fopen(filename,"r");
	if (fd){
		//get the size of the file the file
		fseek(fd, 0, SEEK_END);
		fileSize = ftell(fd);
		rewind(fd);

		while (position < fileSize){

		}
		line = pread(fd,);
	}
	else{
		//can't open file
	}

	fclose(fd);
}

jstring Java_com_mindtherobot_samples_ndkfoo_NdkFooActivity_invokeNativeFunction(JNIEnv* env, jobject javaThis){
	return (*env)->NewStringUTF(env, "Hello from native code!");
}
