//
// Created by yong.zou on 2016/12/12.
//
#include "Localsocket.h"
#include <unistd.h>
#include <sys/un.h>
#include<pthread.h>
#include <android/log.h>

pthread_t m_threadHandler;
bool isrunning = false;

/* 构造sockaddr_un */
int socket_make_sockaddr_un(const char *name, int namespaceId, struct sockaddr_un *p_addr, socklen_t *socklen)
{
    size_t namelen;

    MEM_ZERO(p_addr, sizeof(*p_addr));

    namelen  = strlen(name);

    // Test with length +1 for the *initial* '\0'.
    if ((namelen + 1) > sizeof(p_addr->sun_path))
    {
        return LINUX_MAKE_ADDRUN_ERROR;
    }
    p_addr->sun_path[0] = 0;
    memcpy(p_addr->sun_path + 1, name, namelen);
    p_addr->sun_family = AF_LOCAL;
    *socklen = namelen + offsetof(struct sockaddr_un, sun_path) + 1;

    return NO_ERR;
}

/* 连接到相应的fileDescriptor上 */
int socket_local_client_connect(int fd, const char *name, int namespaceId, int type)
{
    struct sockaddr_un addr;
    socklen_t socklen;
    size_t namelen;
    int ret;

    ret = socket_make_sockaddr_un(name, namespaceId, &addr, &socklen);
    if (ret < 0)
    {
        return ret;
    }

    if(connect(fd, (struct sockaddr *) &addr, socklen) < 0)
    {
        return CONNECT_ERR;
    }

    return fd;
}

int sid;

void* receiveData(void* socketId) {
    int id = (int)socketId;
    while (isrunning){
        char buffer[1];
        memset(buffer, 0, 1);
        ssize_t len = recv(id,buffer,1,0);
        __android_log_print(ANDROID_LOG_DEBUG,"LocalSocketjni","receive type %d",(int)buffer[0]);
        if(len>0){
        if(buffer[0] == 0x10){
            isrunning = false;
            close(id);
            __android_log_print(ANDROID_LOG_DEBUG,"LocalSocketjni","receive type close");
        }else{
//            int value = 1000;
//            char data[value];
//            memset(data, 0, value);
//            ssize_t len = recv(id,data,value,0);
//
//            __android_log_print(ANDROID_LOG_DEBUG,"LocalSocketjni","receive type value%d",len);
            char length[4];
            memset(length, 0, 4);
            recv(id,length,4,0);
            __android_log_print(ANDROID_LOG_DEBUG,"LocalSocketjni","receivelengthbytes %d:%d:%d:%d",(int)length[0],(int)length[1],(int)length[2],(int)length[3]);
            int value = (int)((length[0] & 0xFF)
                        | ((length[1] & 0xFF) << 8)
                        | ((length[2] & 0xFF) << 16)
                        | ((length[3] & 0xFF) << 24));
//            int charLen = (int)((length[4] & 0xFF)
//                                | ((length[5] & 0xFF) << 8)
//                                | ((length[6] & 0xFF) << 16)
//                                | ((length[7] & 0xFF) << 24));
            __android_log_print(ANDROID_LOG_DEBUG,"LocalSocketjni","receivelen %d,strLen %d",value);
            char data[value+1];
            memset(data, 0, (value+1));
            ssize_t slen = recv(id,data,value,0);
            data[value] = '\0';
//            char * p= (char *)data;
            __android_log_print(ANDROID_LOG_DEBUG,"LocalSocketjni","values = %s::%d,%d",data,
                                strlen(data),
                                slen);
        }
        }
    }
    return (void*)1;
}

void send( char type,char *data){
    int len = strlen(data);
    char senddata[5+len];
    senddata[0] = type;
    senddata[4] = (char) ((len >> 24) & 0xFF);
    senddata[3] = (char) ((len >> 16) & 0xFF);
    senddata[2] = (char) ((len >> 8) & 0xFF);
    senddata[1] = (char) (len & 0xFF);
    strncpy(&senddata[5],data,len);
    send(sid,senddata,5+len,0);
}

/* 创建本地socket客户端 */
int socket_local_client(const char *name, int namespaceId, int type)
{
    int socketID;
    int ret;

    socketID = socket(AF_LOCAL, type, 0);
    if(socketID < 0)
    {
        return CREATE_ERR;
    }

    ret = socket_local_client_connect(socketID, name, namespaceId, type);
    if (ret < 0)
    {
        close(socketID);
//                shutdown(socketID,)

        return ret;

    }
    sid = socketID;
    __android_log_print(ANDROID_LOG_DEBUG,"LocalSocket","create thread by %d",socketID);
    send((char)0x01,"jniconnect001\0");
    isrunning = true;
    pthread_create((pthread_t*)&m_threadHandler, NULL, receiveData, (void*)socketID);


    return socketID;
}


