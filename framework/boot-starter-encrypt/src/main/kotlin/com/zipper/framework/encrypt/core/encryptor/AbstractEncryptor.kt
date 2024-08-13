package com.zipper.framework.encrypt.core.encryptor

import com.zipper.framework.encrypt.core.EncryptContext
import com.zipper.framework.encrypt.core.IEncryptor


/**
 * 所有加密执行者的基类
 *
 * @author 老马
 * @version 4.6.0
 */
abstract class AbstractEncryptor(protected val context: com.zipper.framework.encrypt.core.EncryptContext) : IEncryptor
