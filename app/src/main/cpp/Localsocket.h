//
// Created by yong.zou on 2016/12/12.
//
#ifndef LOCALSOCKETTEST_LOCALSOCKET_H
#define LOCALSOCKETTEST_LOCALSOCKET_H

#include <sys/socket.h>

#define ANDROID_SOCKET_NAMESPACE_ABSTRACT 0

/* socket类型 */
#define SOCK_STREAM      1
#define SOCK_DGRAM       2
#define SOCK_RAW         3
#define SOCK_RDM         4
#define SOCK_SEQPACKET   5
#define SOCK_PACKET      10

/* 错误码定义 */
#define NO_ERR 0
#define CREATE_ERR -1
#define CONNECT_ERR -2
#define LINUX_MAKE_ADDRUN_ERROR -3
#define NO_LINUX_MAKE_ADDRUN_ERROR -4
#define CLOSE_ERR -5

/* 清0宏 */
#define MEM_ZERO(pDest, destSize) memset(pDest, 0, destSize)

//#define HAVE_LINUX_LOCAL_SOCKET_NAMESPACE "linux_local_socket_namespace"

int socket_make_sockaddr_un(const char *name, int namespaceId, struct sockaddr_un *p_addr, socklen_t *socklen);

int socket_local_client_connect(int fd, const char *name, int namespaceId, int type);

int socket_local_client(const char *name, int namespaceId, int type);


#endif //LOCALSOCKETTEST_LOCALSOCKET_H
