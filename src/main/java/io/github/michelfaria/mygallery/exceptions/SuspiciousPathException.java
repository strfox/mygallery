/*
 * Copyright (C) 2019 Michel Faria
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.github.michelfaria.mygallery.exceptions;

public class SuspiciousPathException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SuspiciousPathException() {
        // TODO Auto-generated constructor stub
    }

    public SuspiciousPathException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public SuspiciousPathException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public SuspiciousPathException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public SuspiciousPathException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

}
