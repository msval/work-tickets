import moment from 'moment';

export class DateFormatValueConverter {
    toView(value) {
        return moment(value).format('YYYY-MM-DD HH:mm:ss');
    }
}