/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2013
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4che3.android.image;

import org.dcm4che3.data.Tag;
import org.dcm4che3.data.Attributes;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 *
 */
public abstract class StoredValue {

    public abstract int valueOf(int pixel);
    public abstract int minValue();
    public abstract int maxValue();

    public static class Unsigned extends StoredValue {

        private final int mask;

        public Unsigned(int bitsStored) {
            this.mask = (1 << bitsStored) - 1;
        }

        @Override
        public int valueOf(int pixel) {
            return pixel & mask;
        }

        @Override
        public int minValue() {
            return 0;
        }

        @Override
        public int maxValue() {
            return mask;
        }
    }

    public static class Signed extends StoredValue {

        private final int bitsStored;
        private final int shift;

        public Signed(int bitsStored) {
            this.bitsStored = bitsStored;
            this.shift = 32 - bitsStored;
        }

        @Override
        public int valueOf(int pixel) {
            return pixel << shift >> shift;
        }

        @Override
        public int minValue() {
            return -(1 << (bitsStored-1));
        }

        @Override
        public int maxValue() {
            return (1 << (bitsStored-1)) - 1;
        }
    }

    public static StoredValue valueOf(Attributes attrs) {
        int bitsStored = attrs.getInt(Tag.BitsStored, 0);
        if (bitsStored == 0)
            bitsStored = attrs.getInt(Tag.BitsAllocated, 8);
        return attrs.getInt(Tag.PixelRepresentation, 0) != 0
                ? new Signed(bitsStored)
                : new Unsigned(bitsStored);
    }
}
