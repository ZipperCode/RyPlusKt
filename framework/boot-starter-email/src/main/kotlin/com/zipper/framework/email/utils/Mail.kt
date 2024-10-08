package com.zipper.framework.email.utils

import cn.hutool.core.builder.Builder
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IORuntimeException
import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.ArrayUtil
import cn.hutool.core.util.ObjectUtil
import cn.hutool.core.util.StrUtil
import com.zipper.framework.email.utils.GlobalMailAccount.account
import com.zipper.framework.email.utils.InternalMailUtil.encodeText
import com.zipper.framework.email.utils.InternalMailUtil.parseAddressFromStrs
import com.zipper.framework.email.utils.InternalMailUtil.parseFirstAddress
import jakarta.activation.DataHandler
import jakarta.activation.DataSource
import jakarta.activation.FileDataSource
import jakarta.activation.FileTypeMap
import jakarta.mail.*
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import jakarta.mail.internet.MimeUtility
import jakarta.mail.util.ByteArrayDataSource
import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * 邮件发送客户端
 *
 * @author looly
 * @since 3.2.0
 */
class Mail constructor(mailAccount: MailAccount) : Builder<MimeMessage> {
    /**
     * 邮箱帐户信息以及一些客户端配置信息
     */
    private val mailAccount: MailAccount

    /**
     * 收件人列表
     */
    private var tos: Array<out String?> = emptyArray()

    /**
     * 抄送人列表（carbon copy）
     */
    private var ccs: Array<out String?> = emptyArray()

    /**
     * 密送人列表（blind carbon copy）
     */
    private var bccs: Array<out String?> = emptyArray()

    /**
     * 回复地址(reply-to)
     */
    private var reply: Array<out String?> = emptyArray()

    /**
     * 标题
     */
    private var title: String? = null

    /**
     * 内容
     */
    private var content: String? = null

    /**
     * 是否为HTML
     */
    private var isHtml = false

    /**
     * 正文、附件和图片的混合部分
     */
    private val multipart: Multipart = MimeMultipart()

    /**
     * 是否使用全局会话，默认为false
     */
    private var useGlobalSession = false

    /**
     * debug输出位置，可以自定义debug日志
     */
    private var debugOutput: PrintStream? = null

    /**
     * 构造
     *
     * @param mailAccount 邮件帐户，如果为null使用默认配置文件的全局邮件配置
     */
    // --------------------------------------------------------------- Constructor start
    /**
     * 构造，使用全局邮件帐户
     */
    init {
        this.mailAccount = mailAccount.defaultIfEmpty()
    }

    // --------------------------------------------------------------- Constructor end
    // --------------------------------------------------------------- Getters and Setters start
    /**
     * 设置收件人
     *
     * @param tos 收件人列表
     * @return this
     * @see .setTos
     */
    fun to(vararg tos: String?): Mail {
        return setTos(*tos)
    }

    /**
     * 设置多个收件人
     *
     * @param tos 收件人列表
     * @return this
     */
    fun setTos(vararg tos: String?): Mail {
        this.tos = tos
        return this
    }

    /**
     * 设置多个抄送人（carbon copy）
     *
     * @param ccs 抄送人列表
     * @return this
     * @since 4.0.3
     */
    fun setCcs(vararg ccs: String?): Mail {
        this.ccs = ccs
        return this
    }

    /**
     * 设置多个密送人（blind carbon copy）
     *
     * @param bccs 密送人列表
     * @return this
     * @since 4.0.3
     */
    fun setBccs(vararg bccs: String?): Mail {
        this.bccs = bccs
        return this
    }

    /**
     * 设置多个回复地址(reply-to)
     *
     * @param reply 回复地址(reply-to)列表
     * @return this
     * @since 4.6.0
     */
    fun setReply(vararg reply: String?): Mail {
        this.reply = reply
        return this
    }

    /**
     * 设置标题
     *
     * @param title 标题
     * @return this
     */
    fun setTitle(title: String?): Mail {
        this.title = title
        return this
    }

    /**
     * 设置正文<br></br>
     * 正文可以是普通文本也可以是HTML（默认普通文本），可以通过调用[.setHtml] 设置是否为HTML
     *
     * @param content 正文
     * @return this
     */
    fun setContent(content: String?): Mail {
        this.content = content
        return this
    }

    /**
     * 设置是否是HTML
     *
     * @param isHtml 是否为HTML
     * @return this
     */
    fun setHtml(isHtml: Boolean): Mail {
        this.isHtml = isHtml
        return this
    }

    /**
     * 设置正文
     *
     * @param content 正文内容
     * @param isHtml  是否为HTML
     * @return this
     */
    fun setContent(content: String?, isHtml: Boolean): Mail {
        setContent(content)
        return setHtml(isHtml)
    }

    /**
     * 设置文件类型附件，文件可以是图片文件，此时自动设置cid（正文中引用图片），默认cid为文件名
     *
     * @param files 附件文件列表
     * @return this
     */
    fun setFiles(vararg files: File): Mail {
        if (ArrayUtil.isEmpty(files)) {
            return this
        }

        val attachments = Array<DataSource>(files.size) { i ->
            FileDataSource(files[i])
        }
        return setAttachments(*attachments)
    }

    /**
     * 增加附件或图片，附件使用[DataSource] 形式表示，可以使用[FileDataSource]包装文件表示文件附件
     *
     * @param attachments 附件列表
     * @return this
     * @since 4.0.9
     */
    fun setAttachments(vararg attachments: DataSource): Mail {
        if (ArrayUtil.isNotEmpty(attachments)) {
            val charset = mailAccount?.charset ?: StandardCharsets.UTF_8
            var bodyPart: MimeBodyPart
            var nameEncoded: String
            try {
                for (attachment in attachments) {
                    bodyPart = MimeBodyPart()
                    bodyPart.dataHandler = DataHandler(attachment)
                    nameEncoded = attachment.name
                    if (mailAccount?.isEncodefilename == true) {
                        nameEncoded = encodeText(nameEncoded, charset)
                    }
                    // 普通附件文件名
                    bodyPart.fileName = nameEncoded
                    if (StrUtil.startWith(attachment.contentType, "image/")) {
                        // 图片附件，用于正文中引用图片
                        bodyPart.contentID = nameEncoded
                    }
                    multipart.addBodyPart(bodyPart)
                }
            } catch (e: MessagingException) {
                throw MailException(e)
            }
        }
        return this
    }

    /**
     * 增加图片，图片的键对应到邮件模板中的占位字符串
     *
     * @param cid         图片与占位符，占位符格式为cid:${cid}
     * @param imageStream 图片流，不关闭
     * @param contentType 图片类型，null赋值默认的"image/jpeg"
     * @return this
     * @since 4.6.3
     */
    /**
     * 增加图片，图片的键对应到邮件模板中的占位字符串，图片类型默认为"image/jpeg"
     *
     * @param cid         图片与占位符，占位符格式为cid:${cid}
     * @param imageStream 图片文件
     * @return this
     * @since 4.6.3
     */
    @JvmOverloads
    fun addImage(cid: String?, imageStream: InputStream?, contentType: String? = null): Mail {
        val imgSource: ByteArrayDataSource
        try {
            imgSource = ByteArrayDataSource(imageStream, ObjectUtil.defaultIfNull(contentType, "image/jpeg"))
        } catch (e: IOException) {
            throw IORuntimeException(e)
        }
        imgSource.name = cid
        return setAttachments(imgSource)
    }

    /**
     * 增加图片，图片的键对应到邮件模板中的占位字符串
     *
     * @param cid       图片与占位符，占位符格式为cid:${cid}
     * @param imageFile 图片文件
     * @return this
     * @since 4.6.3
     */
    fun addImage(cid: String?, imageFile: File?): Mail {
        var `in`: InputStream? = null
        try {
            `in` = FileUtil.getInputStream(imageFile)
            return addImage(cid, `in`, FileTypeMap.getDefaultFileTypeMap().getContentType(imageFile))
        } finally {
            IoUtil.close(`in`)
        }
    }

    /**
     * 设置字符集编码
     *
     * @param charset 字符集编码
     * @return this
     * @see MailAccount.setCharset
     */
    fun setCharset(charset: Charset): Mail {
        mailAccount?.setCharset(charset)
        return this
    }

    /**
     * 设置是否使用全局会话，默认为true
     *
     * @param isUseGlobalSession 是否使用全局会话，默认为true
     * @return this
     * @since 4.0.2
     */
    fun setUseGlobalSession(isUseGlobalSession: Boolean): Mail {
        this.useGlobalSession = isUseGlobalSession
        return this
    }

    /**
     * 设置debug输出位置，可以自定义debug日志
     *
     * @param debugOutput debug输出位置
     * @return this
     * @since 5.5.6
     */
    fun setDebugOutput(debugOutput: PrintStream?): Mail {
        this.debugOutput = debugOutput
        return this
    }

    // --------------------------------------------------------------- Getters and Setters end
    override fun build(): MimeMessage {
        try {
            return buildMsg()
        } catch (e: MessagingException) {
            throw MailException(e)
        }
    }

    /**
     * 发送
     *
     * @return message-id
     * @throws MailException 邮件发送异常
     */
    @Throws(MailException::class)
    fun send(): String {
        try {
            return doSend()
        } catch (e: MessagingException) {
            if (e is SendFailedException) {
                // 当地址无效时，显示更加详细的无效地址信息
                val invalidAddresses = e.invalidAddresses
                val msg = StrUtil.format("Invalid Addresses: {}", ArrayUtil.toString(invalidAddresses))
                throw MailException(msg, e)
            }
            throw MailException(e)
        }
    }

    // --------------------------------------------------------------- Private method start
    /**
     * 执行发送
     *
     * @return message-id
     * @throws MessagingException 发送异常
     */
    @Throws(MessagingException::class)
    private fun doSend(): String {
        val mimeMessage = buildMsg()
        Transport.send(mimeMessage)
        return mimeMessage.messageID
    }

    /**
     * 构建消息
     *
     * @return [MimeMessage]消息
     * @throws MessagingException 消息异常
     */
    @Throws(MessagingException::class)
    private fun buildMsg(): MimeMessage {
        val charset = mailAccount.charset
        val msg = MimeMessage(session)
        // 发件人
        val from = mailAccount.from
        if (StrUtil.isEmpty(from)) {
            // 用户未提供发送方，则从Session中自动获取
            msg.setFrom()
        } else {
            msg.setFrom(parseFirstAddress(from, charset))
        }
        // 标题
        msg.setSubject(this.title, charset.name())
        // 发送时间
        msg.sentDate = Date()
        // 内容和附件
        msg.setContent(buildContent(charset))
        // 收件人
        msg.setRecipients(
            MimeMessage.RecipientType.TO, parseAddressFromStrs(
                this.tos, charset
            )
        )
        // 抄送人
        if (ArrayUtil.isNotEmpty<String?>(this.ccs)) {
            msg.setRecipients(
                MimeMessage.RecipientType.CC, parseAddressFromStrs(
                    this.ccs, charset
                )
            )
        }
        // 密送人
        if (ArrayUtil.isNotEmpty<String?>(this.bccs)) {
            msg.setRecipients(
                MimeMessage.RecipientType.BCC, parseAddressFromStrs(
                    this.bccs, charset
                )
            )
        }
        // 回复地址(reply-to)
        if (ArrayUtil.isNotEmpty<String?>(this.reply)) {
            msg.replyTo = parseAddressFromStrs(this.reply, charset)
        }

        return msg
    }

    /**
     * 构建邮件信息主体
     *
     * @param charset 编码，`null`则使用[MimeUtility.getDefaultJavaCharset]
     * @return 邮件信息主体
     * @throws MessagingException 消息异常
     */
    @Throws(MessagingException::class)
    private fun buildContent(charset: Charset?): Multipart {
        val charsetStr = if (null != charset) charset.name() else MimeUtility.getDefaultJavaCharset()
        // 正文
        val body = MimeBodyPart()
        body.setContent(content, StrUtil.format("text/{}; charset={}", if (isHtml) "html" else "plain", charsetStr))
        multipart.addBodyPart(body)

        return this.multipart
    }

    private val session: Session
        /**
         * 获取默认邮件会话<br></br>
         * 如果为全局单例的会话，则全局只允许一个邮件帐号，否则每次发送邮件会新建一个新的会话
         *
         * @return 邮件会话 [Session]
         */
        get() {
            val session = MailUtils.getSession(this.mailAccount, this.useGlobalSession)

            if (null != this.debugOutput) {
                session.debugOut = debugOutput
            }

            return session
        } // --------------------------------------------------------------- Private method end

    companion object {
        @Serial
        private val serialVersionUID = 1L

        /**
         * 创建邮件客户端
         *
         * @param mailAccount 邮件帐号
         * @return Mail
         */
        @JvmStatic
        fun create(mailAccount: MailAccount): Mail {
            return Mail(mailAccount)
        }

        /**
         * 创建邮件客户端，使用全局邮件帐户
         *
         * @return Mail
         */
        fun create(): Mail {
            val account = GlobalMailAccount.account ?: throw MailException("No mail account for global, please create instance of Mail by [Mail.create(MailAccount)]")
            return Mail(account)
        }
    }
}
