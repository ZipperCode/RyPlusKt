package com.zipper.framework.email.utils

import cn.hutool.core.util.CharsetUtil
import cn.hutool.core.util.ObjectUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.setting.Setting
import com.zipper.framework.email.utils.InternalMailUtil.parseFirstAddress
import java.io.Serial
import java.io.Serializable
import java.nio.charset.Charset
import java.util.*

/**
 * 邮件账户对象
 *
 * @author Luxiaolei
 */
class MailAccount : Serializable {
    /**
     * 获得SMTP服务器域名
     *
     * @return SMTP服务器域名
     */
    /**
     * SMTP服务器域名
     */
    var host: String? = null
        private set
    /**
     * 获得SMTP服务端口
     *
     * @return SMTP服务端口
     */
    /**
     * SMTP服务端口
     */
    var port: Int? = null
        private set
    /**
     * 是否需要用户名密码验证
     *
     * @return 是否需要用户名密码验证
     */
    /**
     * 是否需要用户名密码验证
     */
    var isAuth: Boolean? = null
        private set
    /**
     * 获取用户名
     *
     * @return 用户名
     */
    /**
     * 用户名
     */
    var user: String? = null
        private set
    /**
     * 获取密码
     *
     * @return 密码
     */
    /**
     * 密码
     */
    var pass: String? = null
        private set
    /**
     * 获取发送方，遵循RFC-822标准
     *
     * @return 发送方，遵循RFC-822标准
     */
    /**
     * 发送方，遵循RFC-822标准
     */
    var from: String? = null
        private set

    /**
     * 是否打开调试模式，调试模式会显示与邮件服务器通信过程，默认不开启
     *
     * @return 是否打开调试模式，调试模式会显示与邮件服务器通信过程，默认不开启
     * @since 4.0.2
     */
    /**
     * 是否打开调试模式，调试模式会显示与邮件服务器通信过程，默认不开启
     */
    var isDebug: Boolean = false
        private set
    /**
     * 获取字符集编码
     *
     * @return 编码，可能为`null`
     */
    /**
     * 编码用于编码邮件正文和发送人、收件人等中文
     */
    var charset: Charset = CharsetUtil.CHARSET_UTF_8
        private set
    /**
     * 对于超长参数是否切分为多份，默认为false（国内邮箱附件不支持切分的附件名）
     *
     * @return 对于超长参数是否切分为多份
     */
    /**
     * 设置对于超长参数是否切分为多份，默认为false（国内邮箱附件不支持切分的附件名）<br></br>
     * 注意此项为全局设置，此项会调用
     * <pre>
     * System.setProperty("mail.mime.splitlongparameters", true)
    </pre> *
     *
     * @param splitlongparameters 对于超长参数是否切分为多份
     */
    /**
     * 对于超长参数是否切分为多份，默认为false（国内邮箱附件不支持切分的附件名）
     */
    var isSplitlongparameters: Boolean = false
    /**
     * 对于文件名是否使用[.charset]编码，默认为 `true`
     *
     * @return 对于文件名是否使用{@link #charset}编码，默认为 `true`
     * @since 5.7.16
     */
    /**
     * 设置对于文件名是否使用[.charset]编码，此选项不会修改全局配置<br></br>
     * 如果此选项设置为`false`，则是否编码取决于两个系统属性：
     *
     *  * mail.mime.encodefilename  是否编码附件文件名
     *  * mail.mime.charset         编码文件名的编码
     *
     *
     * @param encodefilename 对于文件名是否使用[.charset]编码
     * @since 5.7.16
     */
    /**
     * 对于文件名是否使用[.charset]编码，默认为 `true`
     */
    var isEncodefilename: Boolean = true

    /**
     * 是否使用 STARTTLS安全连接，STARTTLS是对纯文本通信协议的扩展。它将纯文本连接升级为加密连接（TLS或SSL）， 而不是使用一个单独的加密通信端口。
     *
     * @return 是否使用 STARTTLS安全连接
     */
    /**
     * 使用 STARTTLS安全连接，STARTTLS是对纯文本通信协议的扩展。它将纯文本连接升级为加密连接（TLS或SSL）， 而不是使用一个单独的加密通信端口。
     */
    var isStarttlsEnable: Boolean = false
        private set
    /**
     * 是否使用 SSL安全连接
     *
     * @return 是否使用 SSL安全连接
     */
    /**
     * 使用 SSL安全连接
     */
    var isSslEnable: Boolean? = null
        private set

    /**
     * 获取SSL协议，多个协议用空格分隔
     *
     * @return SSL协议，多个协议用空格分隔
     * @since 5.5.7
     */
    /**
     * 设置SSL协议，多个协议用空格分隔
     *
     * @param sslProtocols SSL协议，多个协议用空格分隔
     * @since 5.5.7
     */
    /**
     * SSL协议，多个协议用空格分隔
     */
    var sslProtocols: String? = null

    /**
     * 获取指定实现javax.net.SocketFactory接口的类的名称,这个类将被用于创建SMTP的套接字
     *
     * @return 指定实现javax.net.SocketFactory接口的类的名称, 这个类将被用于创建SMTP的套接字
     */
    /**
     * 指定实现javax.net.SocketFactory接口的类的名称,这个类将被用于创建SMTP的套接字
     */
    var socketFactoryClass: String = "javax.net.ssl.SSLSocketFactory"
        private set
    /**
     * 如果设置为true,未能创建一个套接字使用指定的套接字工厂类将导致使用java.net.Socket创建的套接字类, 默认值为true
     *
     * @return 如果设置为true, 未能创建一个套接字使用指定的套接字工厂类将导致使用java.net.Socket创建的套接字类, 默认值为true
     */
    /**
     * 如果设置为true,未能创建一个套接字使用指定的套接字工厂类将导致使用java.net.Socket创建的套接字类, 默认值为true
     */
    var isSocketFactoryFallback: Boolean = false
        private set
    /**
     * 获取指定的端口连接到在使用指定的套接字工厂。如果没有设置,将使用默认端口
     *
     * @return 指定的端口连接到在使用指定的套接字工厂。如果没有设置,将使用默认端口
     */
    /**
     * 指定的端口连接到在使用指定的套接字工厂。如果没有设置,将使用默认端口
     */
    var socketFactoryPort: Int = 465
        private set

    /**
     * SMTP超时时长，单位毫秒，缺省值不超时
     */
    private var timeout: Long = 0

    /**
     * Socket连接超时值，单位毫秒，缺省值不超时
     */
    private var connectionTimeout: Long = 0

    /**
     * Socket写出超时值，单位毫秒，缺省值不超时
     */
    private var writeTimeout: Long = 0

    /**
     * 自定义的其他属性，此自定义属性会覆盖默认属性
     */
    private val customProperty: MutableMap<String?, Any?> = HashMap()

    // -------------------------------------------------------------- Constructor start
    /**
     * 构造,所有参数需自行定义或保持默认值
     */
    constructor()

    /**
     * 构造
     *
     * @param settingPath 配置文件路径
     */
    constructor(settingPath: String?) : this(Setting(settingPath))

    /**
     * 构造
     *
     * @param setting 配置文件
     */
    constructor(setting: Setting) {
        setting.toBean(this)
    }

    // -------------------------------------------------------------- Constructor end

    /**
     * 设置SMTP服务器域名
     *
     * @param host SMTP服务器域名
     * @return this
     */
    fun setHost(host: String?): MailAccount {
        this.host = host
        return this
    }

    /**
     * 设置SMTP服务端口
     *
     * @param port SMTP服务端口
     * @return this
     */
    fun setPort(port: Int?): MailAccount {
        this.port = port
        return this
    }

    /**
     * 设置是否需要用户名密码验证
     *
     * @param isAuth 是否需要用户名密码验证
     * @return this
     */
    fun setAuth(isAuth: Boolean): MailAccount {
        this.isAuth = isAuth
        return this
    }

    /**
     * 设置用户名
     *
     * @param user 用户名
     * @return this
     */
    fun setUser(user: String?): MailAccount {
        this.user = user
        return this
    }

    /**
     * 设置密码
     *
     * @param pass 密码
     * @return this
     */
    fun setPass(pass: String?): MailAccount {
        this.pass = pass
        return this
    }

    /**
     * 设置发送方，遵循RFC-822标准<br></br>
     * 发件人可以是以下形式：
     *
     * <pre>
     * 1. user@xxx.xx
     * 2.  name &lt;user@xxx.xx&gt;
    </pre> *
     *
     * @param from 发送方，遵循RFC-822标准
     * @return this
     */
    fun setFrom(from: String?): MailAccount {
        this.from = from
        return this
    }

    /**
     * 设置是否打开调试模式，调试模式会显示与邮件服务器通信过程，默认不开启
     *
     * @param debug 是否打开调试模式，调试模式会显示与邮件服务器通信过程，默认不开启
     * @return this
     * @since 4.0.2
     */
    fun setDebug(debug: Boolean): MailAccount {
        this.isDebug = debug
        return this
    }

    /**
     * 设置字符集编码，此选项不会修改全局配置，若修改全局配置，请设置此项为`null`并设置：
     * <pre>
     * System.setProperty("mail.mime.charset", charset);
    </pre> *
     *
     * @param charset 字符集编码，`null` 则表示使用全局设置的默认编码，全局编码为mail.mime.charset系统属性
     * @return this
     */
    fun setCharset(charset: Charset): MailAccount {
        this.charset = charset
        return this
    }

    /**
     * 设置是否使用STARTTLS安全连接，STARTTLS是对纯文本通信协议的扩展。它将纯文本连接升级为加密连接（TLS或SSL）， 而不是使用一个单独的加密通信端口。
     *
     * @param startttlsEnable 是否使用STARTTLS安全连接
     * @return this
     */
    fun setStarttlsEnable(startttlsEnable: Boolean): MailAccount {
        this.isStarttlsEnable = startttlsEnable
        return this
    }

    /**
     * 设置是否使用SSL安全连接
     *
     * @param sslEnable 是否使用SSL安全连接
     * @return this
     */
    fun setSslEnable(sslEnable: Boolean?): MailAccount {
        this.isSslEnable = sslEnable
        return this
    }

    /**
     * 设置指定实现javax.net.SocketFactory接口的类的名称,这个类将被用于创建SMTP的套接字
     *
     * @param socketFactoryClass 指定实现javax.net.SocketFactory接口的类的名称,这个类将被用于创建SMTP的套接字
     * @return this
     */
    fun setSocketFactoryClass(socketFactoryClass: String): MailAccount {
        this.socketFactoryClass = socketFactoryClass
        return this
    }

    /**
     * 如果设置为true,未能创建一个套接字使用指定的套接字工厂类将导致使用java.net.Socket创建的套接字类, 默认值为true
     *
     * @param socketFactoryFallback 如果设置为true,未能创建一个套接字使用指定的套接字工厂类将导致使用java.net.Socket创建的套接字类, 默认值为true
     * @return this
     */
    fun setSocketFactoryFallback(socketFactoryFallback: Boolean): MailAccount {
        this.isSocketFactoryFallback = socketFactoryFallback
        return this
    }

    /**
     * 指定的端口连接到在使用指定的套接字工厂。如果没有设置,将使用默认端口
     *
     * @param socketFactoryPort 指定的端口连接到在使用指定的套接字工厂。如果没有设置,将使用默认端口
     * @return this
     */
    fun setSocketFactoryPort(socketFactoryPort: Int): MailAccount {
        this.socketFactoryPort = socketFactoryPort
        return this
    }

    /**
     * 设置SMTP超时时长，单位毫秒，缺省值不超时
     *
     * @param timeout SMTP超时时长，单位毫秒，缺省值不超时
     * @return this
     * @since 4.1.17
     */
    fun setTimeout(timeout: Long): MailAccount {
        this.timeout = timeout
        return this
    }

    /**
     * 设置Socket连接超时值，单位毫秒，缺省值不超时
     *
     * @param connectionTimeout Socket连接超时值，单位毫秒，缺省值不超时
     * @return this
     * @since 4.1.17
     */
    fun setConnectionTimeout(connectionTimeout: Long): MailAccount {
        this.connectionTimeout = connectionTimeout
        return this
    }

    /**
     * 设置Socket写出超时值，单位毫秒，缺省值不超时
     *
     * @param writeTimeout Socket写出超时值，单位毫秒，缺省值不超时
     * @return this
     * @since 5.8.3
     */
    fun setWriteTimeout(writeTimeout: Long): MailAccount {
        this.writeTimeout = writeTimeout
        return this
    }

    /**
     * 获取自定义属性列表
     *
     * @return 自定义参数列表
     * @since 5.6.4
     */
    fun getCustomProperty(): Map<String?, Any?> {
        return customProperty
    }

    /**
     * 设置自定义属性，如mail.smtp.ssl.socketFactory
     *
     * @param key   属性名，空白被忽略
     * @param value 属性值， null被忽略
     * @return this
     * @since 5.6.4
     */
    fun setCustomProperty(key: String?, value: Any?): MailAccount {
        if (StrUtil.isNotBlank(key) && ObjectUtil.isNotNull(value)) {
            customProperty[key] = value
        }
        return this
    }

    val smtpProps: Properties
        /**
         * 获得SMTP相关信息
         *
         * @return [Properties]
         */
        get() {
            //全局系统参数
            System.setProperty(
                SPLIT_LONG_PARAMS,
                isSplitlongparameters.toString()
            )

            val p = Properties()
            p[MAIL_PROTOCOL] = "smtp"
            p[SMTP_HOST] = host
            p[SMTP_PORT] = port.toString()
            p[SMTP_AUTH] = isAuth.toString()
            if (this.timeout > 0) {
                p[SMTP_TIMEOUT] = timeout.toString()
            }
            if (this.connectionTimeout > 0) {
                p[SMTP_CONNECTION_TIMEOUT] = connectionTimeout.toString()
            }
            // issue#2355
            if (this.writeTimeout > 0) {
                p[SMTP_WRITE_TIMEOUT] = writeTimeout.toString()
            }

            p[MAIL_DEBUG] = isDebug.toString()

            if (this.isStarttlsEnable) {
                //STARTTLS是对纯文本通信协议的扩展。它将纯文本连接升级为加密连接（TLS或SSL）， 而不是使用一个单独的加密通信端口。
                p[STARTTLS_ENABLE] = "true"

                if (null == this.isSslEnable) {
                    //为了兼容旧版本，当用户没有此项配置时，按照starttlsEnable开启状态时对待
                    this.isSslEnable = true
                }
            }

            // SSL
            if (null != this.isSslEnable && isSslEnable!!) {
                p[SSL_ENABLE] = "true"
                p[SOCKET_FACTORY] = socketFactoryClass
                p[SOCKET_FACTORY_FALLBACK] = isSocketFactoryFallback.toString()
                p[SOCKET_FACTORY_PORT] = socketFactoryPort.toString()
                // issue#IZN95@Gitee，在Linux下需自定义SSL协议版本
                if (StrUtil.isNotBlank(this.sslProtocols)) {
                    p[SSL_PROTOCOLS] = sslProtocols
                }
            }

            // 补充自定义属性，允许自定属性覆盖已经设置的值
            p.putAll(this.customProperty)

            return p
        }

    /**
     * 如果某些值为null，使用默认值
     *
     * @return this
     */
    fun defaultIfEmpty(): MailAccount {
        // 去掉发件人的姓名部分
        val fromAddress = parseFirstAddress(this.from, this.charset).address

        if (StrUtil.isBlank(this.host)) {
            // 如果SMTP地址为空，默认使用smtp.<发件人邮箱后缀>
            this.host = StrUtil.format("smtp.{}", StrUtil.subSuf(fromAddress, fromAddress.indexOf('@') + 1))
        }
        if (StrUtil.isBlank(user)) {
            // 如果用户名为空，默认为发件人（issue#I4FYVY@Gitee）
            //this.user = StrUtil.subPre(fromAddress, fromAddress.indexOf('@'));
            this.user = fromAddress
        }
        if (null == this.isAuth) {
            // 如果密码非空白，则使用认证模式
            this.isAuth = (false == StrUtil.isBlank(this.pass))
        }
        if (null == this.port) {
            // 端口在SSL状态下默认与socketFactoryPort一致，非SSL状态下默认为25
            this.port = if ((null != this.isSslEnable && isSslEnable!!)) this.socketFactoryPort else 25
        }

        return this
    }

    override fun toString(): String {
        return ("MailAccount [host=" + host + ", port=" + port + ", auth=" + isAuth + ", user=" + user + ", pass=" + (if (StrUtil.isEmpty(
                this.pass
            )
        ) "" else "******") + ", from=" + from + ", startttlsEnable="
                + isStarttlsEnable + ", socketFactoryClass=" + socketFactoryClass + ", socketFactoryFallback=" + isSocketFactoryFallback + ", socketFactoryPort=" + socketFactoryPort + "]")
    }

    companion object {
        @Serial
        private val serialVersionUID = -6937313421815719204L

        private const val MAIL_PROTOCOL = "mail.transport.protocol"
        private const val SMTP_HOST = "mail.smtp.host"
        private const val SMTP_PORT = "mail.smtp.port"
        private const val SMTP_AUTH = "mail.smtp.auth"
        private const val SMTP_TIMEOUT = "mail.smtp.timeout"
        private const val SMTP_CONNECTION_TIMEOUT = "mail.smtp.connectiontimeout"
        private const val SMTP_WRITE_TIMEOUT = "mail.smtp.writetimeout"

        // SSL
        private const val STARTTLS_ENABLE = "mail.smtp.starttls.enable"
        private const val SSL_ENABLE = "mail.smtp.ssl.enable"
        private const val SSL_PROTOCOLS = "mail.smtp.ssl.protocols"
        private const val SOCKET_FACTORY = "mail.smtp.socketFactory.class"
        private const val SOCKET_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback"
        private const val SOCKET_FACTORY_PORT = "smtp.socketFactory.port"

        // System Properties
        private const val SPLIT_LONG_PARAMS = "mail.mime.splitlongparameters"

        //private static final String ENCODE_FILE_NAME = "mail.mime.encodefilename";
        //private static final String CHARSET = "mail.mime.charset";
        // 其他
        private const val MAIL_DEBUG = "mail.debug"

        val MAIL_SETTING_PATHS: Array<String> = arrayOf("config/mail.setting", "config/mailAccount.setting", "mail.setting")
    }
}
