# 该项目是mybatisplus单独学习的项目

## 参考链接:

[完整项目](https://www.bilibili.com/video/BV1S142197x7/?spm_id_from=333.1391.0.0&vd_source=f622692ec0ed8aeba5d1daa8bfb232ef)

[//]: # (## 单独的mybatisplus学习:)

[单独的mybatisplus学习](https://www.bilibili.com/video/BV1Xu411A7tL?spm_id_from=333.788.videopod.episodes&vd_source=f622692ec0ed8aeba5d1daa8bfb232ef)

[//]: # (## mybatis官方网站:)

[mybatis官方网站](https://baomidou.com/introduce/)

[//]: # (## mybatis学习笔记文档:)

[mybatis学习笔记文档](https://b11et3un53m.feishu.cn/wiki/FYNkwb1i6i0qwCk7lF2caEq5nRe)

## mybatisplus依赖:

```xml

<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3.1</version>
</dependency>
```

## 额外使用的插件:

[Hutool](https://hutool.cn/)

[Swagger](https://swagger.io/)

[IDEA插件MyBatisPlus](https://plugins.jetbrains.com/plugin/12670-mybatisplus)

## 注意事项:

- mybatis-plus是用来增强mybatis的，需要和mybatis同时使用
- Mybatisplus因为其BaseMapper封装好的方法，单表操作效率极高。但是多表操作不是很适合，复杂的单表操作和多表联查依然写到mybatis的xml。
- 此外，尽量少在service层让sql过渡侵入，因为mybatisplus里IService和ServiceImpl有类似BaseMapper的单表操作方法，ServiceImpl中注入了BaseMapper，并可以自定义，相当于底层还是通过BaseMapper。

