# 大数据平台

## Redis
### token
> 用户鉴权凭证

存储格式
- key：token:{token}
- value: {authentication}

过期时间
- token过期时间3h
- redis过期时间3h

