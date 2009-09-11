<?php

require_once(sfConfig::get('sf_lib_dir').'/filter/base/BaseFormFilterPropel.class.php');

/**
 * ApplicationHasIntent filter form base class.
 *
 * @package    mashup
 * @subpackage filter
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormFilterGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseApplicationHasIntentFormFilter extends BaseFormFilterPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'application_id'            => new sfWidgetFormPropelChoice(array('model' => 'Application', 'add_empty' => true)),
      'intent_id'                 => new sfWidgetFormPropelChoice(array('model' => 'Intent', 'add_empty' => true)),
    ));

    $this->setValidators(array(
      'application_id'            => new sfValidatorPropelChoice(array('required' => false, 'model' => 'Application', 'column' => 'id_application')),
      'intent_id'                 => new sfValidatorPropelChoice(array('required' => false, 'model' => 'Intent', 'column' => 'id_intent')),
    ));

    $this->widgetSchema->setNameFormat('application_has_intent_filters[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'ApplicationHasIntent';
  }

  public function getFields()
  {
    return array(
      'id_application_has_intent' => 'Number',
      'application_id'            => 'ForeignKey',
      'intent_id'                 => 'ForeignKey',
    );
  }
}
