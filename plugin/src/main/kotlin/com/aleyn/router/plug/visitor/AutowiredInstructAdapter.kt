package com.aleyn.router.plug.visitor

import com.aleyn.router.plug.data.HandleModel
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.InstructionAdapter


/**
 * @author: Aleyn
 * @date: 2023/7/10 9:50
 */
class AutowiredInstructAdapter(
    api: Int,
    methodVisitor: MethodVisitor,
    private val autowiredClass: List<HandleModel.Autowired>?
) : InstructionAdapter(api, methodVisitor) {

    override fun visitCode() {
        autowiredClass?.forEach {
            val label0 = Label()
            val label1 = Label()
            val label2 = Label()

            visitTryCatchBlock(label0, label1, label2, "java/lang/Exception")
            mark(label0)
            nop()
            load(0, OBJECT_TYPE)
            invokestatic(
                it.className,
                "autowiredInject",
                "(Ljava/lang/Object;)V",
                false
            )
            mark(label1)
            val label3 = Label()
            goTo(label3)
            mark(label2)
            store(1, OBJECT_TYPE)
            mark(label3)
        }
        super.visitCode()
    }

}