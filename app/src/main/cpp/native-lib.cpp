#include <jni.h>
#include <string>
#include <android/log.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include "Localsocket.h"

extern "C" {
jint
Java_com_example_yongzou_localsockettest_jniutil_JniLocalSocket_createSocket(
        JNIEnv *env,
        jobject /* this */) {
        __android_log_print(ANDROID_LOG_INFO, "tracy", "ClientRcvThread created!start");
        int socketID;
        struct sockaddr_un serverAddr;
        char path[] = "test_server_name\0";
        int ret;

        socketID = socket_local_client(path, ANDROID_SOCKET_NAMESPACE_ABSTRACT, SOCK_STREAM);
        __android_log_print(ANDROID_LOG_INFO,"LocalSocket","JniLocalSocket_createSocket %d",socketID);
        __android_log_print(ANDROID_LOG_INFO, "tracy", "ClientRcvThread created!");
        if (socketID < 0)
        {
                return socketID;
        }


//        ret = close(socketID);
//        if (ret < 0)
//        {
//                return CLOSE_ERR;
//        }

        return NO_ERR;
}

void
Java_com_example_yongzou_localsockettest_jniutil_JniLocalSocket_logTest(JNIEnv *env, jclass type) {
        __android_log_print(ANDROID_LOG_INFO, "tracy", "ClientRcvThread logTest");
        // TODO
        int id = 38;
        void* index= (void*)id;
        int test = (int)index;
        __android_log_print(ANDROID_LOG_INFO, "tracy", "ClientRcvThread logTest%d",test);
}

jint
Java_com_example_yongzou_localsockettest_jniutil_JniLocalSocket_logT(JNIEnv *env,
                                                                     jobject instance) {
//        __android_log_print(ANDROID_LOG_INFO, "tracy", "ClientRcvThread logTest22222");
        // TODO

}

}

