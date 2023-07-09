# quiz

`Minecraft` 有奖问答插件

## 介绍
定时发起提问，第一个问答正确的玩家将获得奖励。玩家只需要在聊天里直接输入答案就行，不需要使用命令。

## 功能
## 自定义奖励
当玩家抢答成功后将获得奖励，奖励支持会从奖励池里随机获取一条发放。

通过 `/quizadmin award create <奖励命令>` 来创建一项奖励到奖励池里。

`/quizadmin award create expirence give %p %r[100-500]; give %p diamond %r[1-20]`

上述命令为创建一项奖励，给玩家发放随机 100-500 的经验并且给予 1-20 个钻石。

创建奖励命令支持以下特性：
1. 一项奖励可以设置多条命令，如上述的发放经验以及发放钻石，通过英文符号 `;` 来设定多条命令。
2. 提供两个变量 `%p` 和 `%r[n-m]`
    + %p 表示抢答成功的玩家
    + %r[n-m] 表示一个整数随机值, 范围为 n ~ m(含)

当奖励池里有多项奖励时，会随机抽取一个来执行

## 出题顺序
出题顺序影响玩家的体验。如果每一次出题都是从题库里随机获取，很可能会经常提问相同的题目。

本插件的随机算法为 `洗牌` 算法，即服务器一开始就将未来所有的出题已经排好，按顺序依次出题，类似于听歌软件的歌单，尽可能避免重复出题。

你可以通过 `/quizadmin question queue` 来查看未来的出题题目。

要注意的是：
1. 服务器关闭、重启时会将当前出题表缓存起来，在下一次服务器启动时继续按照当时的出题顺序出题；
2. 添加题目会自动追加到出题表末尾
3. 删除题目也会将题目从出题表里移除

## 导入题目

一直使用命令来创建题目很累吧，本插件支持从 `csv` 文件里快速导入，并且自动去重，不会出现相同的题目。

通过 `/quizadmin question import <文件名>` 导入题目和答案。导入时是异步处理，不会卡服，放心导入吧！

## 多答案支持

为了支持谐音梗等问题，允许一个问题有多个答案


## 玩家答题统计

本插件支持 `PlaceholderAPI`(可选)

+ `%quiz_top.1.name` 获取答题正确数排名第 1 的玩家
+ `%quiz_top.1.corrects` 获取答题正确数排名第 1 的玩家的答题书

其中 `1` 可以替换为你想要的名次的数



