package com.mrebhan.paprika;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Modifier;

public final class Version {

    private int maxVersion;

    public Version() {
        this.maxVersion = 1;
    }

    public void updateMaxVersion(int version) {
        if (version > maxVersion) {
            maxVersion = version;
        }
    }

    public MethodSpec buildMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getVersion")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(int.class);

        builder.addStatement("return $L", maxVersion);

        return builder.build();
    }
}
