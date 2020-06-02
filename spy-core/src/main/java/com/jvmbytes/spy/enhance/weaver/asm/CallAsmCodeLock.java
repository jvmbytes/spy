package com.jvmbytes.spy.enhance.weaver.asm;

import org.objectweb.asm.commons.AdviceAdapter;


/**
 * 用于Call的代码锁
 *
 * @author luanjia
 */
public class CallAsmCodeLock extends AsmCodeLock {

    CallAsmCodeLock(AdviceAdapter aa) {
        super(
                aa,
                new int[]{
                        ICONST_2, POP
                },
                new int[]{
                        ICONST_3, POP
                }
        );
    }
}