package com.challenge.manageruser.web.common;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

public class SnakePage<T> extends PagedModel<T> {

    public SnakePage(final Page<T> page) {
        super(page);
    }
}
