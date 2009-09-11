<?php

/**
 * Intent form base class.
 *
 * @package    mashup
 * @subpackage form
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseIntentForm extends BaseFormPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'id_intent'   => new sfWidgetFormInputHidden(),
      'title'       => new sfWidgetFormInput(),
      'description' => new sfWidgetFormTextarea(),
      'action'      => new sfWidgetFormInput(),
      'icon'        => new sfWidgetFormInput(),
      'mashup'      => new sfWidgetFormInput(),
    ));

    $this->setValidators(array(
      'id_intent'   => new sfValidatorPropelChoice(array('model' => 'Intent', 'column' => 'id_intent', 'required' => false)),
      'title'       => new sfValidatorString(array('max_length' => 45)),
      'description' => new sfValidatorString(array('required' => false)),
      'action'      => new sfValidatorString(array('max_length' => 45)),
      'icon'        => new sfValidatorString(array('max_length' => 256)),
      'mashup'      => new sfValidatorInteger(),
    ));

    $this->widgetSchema->setNameFormat('intent[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'Intent';
  }


}
